package com.example.attendance.alarms.alarmSchedurer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.attendance.alarms.broadcastReceiver.AttendanceBroadcastReceiver
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.database.timeTable.TimeTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(
    private val context: Context,
    private val db: DatabaseRepository) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun scheduleAllAlarms() {
        var slots = listOf<TimeTable>()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                slots = db.getAllSlots()
            }

            // scheduling all the alarms for next one week
            for (slot in slots) {
                scheduleExactRTCAlarm(slot.id)
            }
        }
    }

    // suppressing the missing permission because we are using USE_EXACT_ALARM permission
    // which is granted by default
    @SuppressLint("MissingPermission")
    fun scheduleExactRTCAlarm(slotId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val slot = db.getSlotById(slotId) ?: return@launch

            if (slot.subjectId == null) {
                return@launch
            }

            val pendingIntent = createPendingIntentForExactRTCAlarm(slot)

            // scheduling the alarm 15 minutes before the class
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calculateAlarmTriggerTimeMillis(slot),
                pendingIntent
            )
        }
    }

    /**
     * Calculates the trigger time in milliseconds for an alarm based on a given time slot.
     *
     * The function determines the next occurrence of the slot's day of the week.
     * If the current day is the same as the slot's day and the current time is past the slot's start time,
     * it schedules the alarm for the same day next week.
     * Otherwise, it calculates the number of days until the next occurrence of the slot's day.
     *
     * @param slot The [TimeTable] object representing the time slot for which to calculate the alarm trigger time.
     *             It must contain the day of the week (`slot.day`) and the start time in milliseconds since midnight (`slot.startTimeMillis`).
     * @return The trigger time in milliseconds since the Unix epoch.
     */
    fun calculateAlarmTriggerTimeMillis(slot: TimeTable): Long {
        var startTimeMillis = slot.startTimeMillis - AlarmManager.INTERVAL_FIFTEEN_MINUTES
        var slotDay = slot.day

        if (startTimeMillis < 0L) {
            startTimeMillis = AlarmManager.INTERVAL_DAY + startTimeMillis
            slotDay = DayOfWeek.of(slotDay - 1).minus(1).ordinal
        }

        val date = LocalDate.now()

        val daysToAdd = if (date.dayOfWeek.ordinal == slotDay && LocalTime.now().toNanoOfDay() / 1000000L >= startTimeMillis) {
            7L
        } else {
            date.dayOfWeek.ordinal.daysTo(slotDay).toLong()
        }

        val startOfDayMillis = date.plusDays(daysToAdd)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return startOfDayMillis + startTimeMillis
    }

    fun Int.daysTo(other: Int): Int {
        return (other - this + 7) % 7
    }

    fun cancelExactRTCAlarm(slot: TimeTable) {
        if (slot.subjectId == null) {
            return
        }

        val pendingIntent = createPendingIntentForExactRTCAlarm(slot)

        alarmManager.cancel(pendingIntent)
    }

    fun createPendingIntentForExactRTCAlarm(slot: TimeTable): PendingIntent {
        val intent = Intent(context, AttendanceBroadcastReceiver::class.java)
        intent.action = attendanceApp_notificationAlarm
        intent.putExtra("slotId", slot.id)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slot.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent
    }

    companion object {
        fun canShowExactNotification(context: Context): Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        fun openNotificationSettings(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
        }
    }
}