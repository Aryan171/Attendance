package com.example.attendance.alarms.alarmReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.attendance.alarms.alarmSchedurer.AlarmScheduler
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.database.AppDatabase
import com.example.attendance.database.DatabaseRepository

class AlarmReceiver: BroadcastReceiver() {
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
                alarmScheduler.scheduleExactRTCAlarm(slotId)

            }
        }
    }
}