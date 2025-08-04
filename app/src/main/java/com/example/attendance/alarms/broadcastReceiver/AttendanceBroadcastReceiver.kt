package com.example.attendance.alarms.broadcastReceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.attendance.alarms.alarmSchedurer.AlarmScheduler
import com.example.attendance.alarms.attendanceApp_markAbsent
import com.example.attendance.alarms.attendanceApp_markPresent
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.database.AppDatabase
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.notification.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AttendanceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }
        val db = AppDatabase.getInstance(context.applicationContext)
        val databaseRepository = DatabaseRepository(
            db.attendanceDao(),
            db.subjectDao(),
            db.timetableDao()
        )

        val alarmScheduler = AlarmScheduler(context.applicationContext, databaseRepository)

        when (intent?.action) {
            attendanceApp_notificationAlarm -> {
                // rescheduling the same alarm for next week
                val slotId = intent.getLongExtra("slotId", -1L)
                if (slotId == -1L) {
                    return
                }
                val notification = Notification(context.applicationContext, databaseRepository)
                notification.showNotification(slotId)

                alarmScheduler.scheduleExactRTCAlarm(slotId)
            }

            attendanceApp_markPresent -> {
                val slotId = intent.getLongExtra("slotId", -1L)
                val notificationId = intent.getIntExtra("notificationId", -1)
                if (slotId == -1L || notificationId == -1) {
                    return
                }

                cancelNotification(notificationId, context)

                CoroutineScope(Dispatchers.Main).launch {
                    val slot = withContext(Dispatchers.IO) {
                        databaseRepository.getSlotById(slotId)
                    }

                    if (slot == null || slot.subjectId == null) {
                        return@launch
                    }

                    withContext(Dispatchers.IO) {
                        databaseRepository.markPresent(slot.subjectId, getLocalDate(slot.day))
                    }
                }
            }

            attendanceApp_markAbsent -> {
                val slotId = intent.getLongExtra("slotId", -1L)
                val notificationId = intent.getIntExtra("notificationId", -1)
                if (slotId == -1L || notificationId == -1) {
                    return
                }

                cancelNotification(notificationId, context)

                CoroutineScope(Dispatchers.Main).launch {
                    val slot = withContext(Dispatchers.IO) {
                        databaseRepository.getSlotById(slotId)
                    }

                    if (slot == null || slot.subjectId == null) {
                        return@launch
                    }

                    withContext(Dispatchers.IO) {
                        databaseRepository.markAbsent(slot.subjectId, getLocalDate(slot.day))
                    }
                }
            }
        }
    }

    private fun cancelNotification(notificationId: Int, context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    /**
     * Returns a LocalDate object representing the date of the most recent occurrence of the specified day of the week.
     *
     * @param day The day of the week (0 for Monday, 1 for Tuesday, ..., 6 for Sunday).
     * @return A LocalDate object representing the date of the most recent occurrence of the specified day of the week.
     */
    private fun getLocalDate(day: Int): LocalDate {
        val date = LocalDate.now()
        while(date.dayOfWeek.ordinal != day) {
            date.minusDays(1)
        }
        return date
    }
}