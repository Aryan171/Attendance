package com.example.attendance.alarms.alarmSchedurer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.attendance.alarms.alarmReceiver.AlarmReceiver
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.database.timeTable.TimeTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                if (slot.subjectId != null) {
                    scheduleExactRTCAlarm(slot.id)
                }
            }
        }
    }

    // suppressing the missing permission because we are using USE_EXACT_ALARM permission
    // which is granted by default
    @SuppressLint("MissingPermission")
    fun scheduleExactRTCAlarm(slotId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            var slot: TimeTable?
            withContext(Dispatchers.IO) {
                slot = db.getSlotById(slotId)
            }
            if (slot == null || slot.subjectId == null || isExactRCTAlarmScheduled(slot)) {
                return@launch
            }

            val intent = createIntentForExactRTCAlarm(slot)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                createRequestCodeForExactRCTAlarm(slot.subjectId, slot.startTimeMillis),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

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
        val date = LocalDate.now()

        val daysToAdd = if (date.dayOfWeek.ordinal == slot.day && LocalTime.now().toNanoOfDay() / 1000000L > slot.startTimeMillis) {
            7L
        } else {
            date.dayOfWeek.ordinal.daysTo(slot.day).toLong()
        }

        val startOfDayMillis = date.plusDays(daysToAdd)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return startOfDayMillis + slot.startTimeMillis
    }

    fun Int.daysTo(other: Int): Int {
        return (other - this + 7) % 7
    }

    fun cancelExactRTCAlarm(slot: TimeTable) {
        if (slot.subjectId == null) {
            return
        }
        val intent = createIntentForExactRTCAlarm(slot)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            createRequestCodeForExactRCTAlarm(slot.subjectId, slot.startTimeMillis),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    fun createRequestCodeForExactRCTAlarm(subjectId: Long, epochTimeMillis: Long): Int {
        return (((subjectId + epochTimeMillis) % (Int.MAX_VALUE * 2L)) - Int.MAX_VALUE).toInt()
    }

    fun createIntentForExactRTCAlarm(slot: TimeTable): Intent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = attendanceApp_notificationAlarm
        intent.putExtra("slotId", slot.id)
        return intent
    }

    fun isExactRCTAlarmScheduled(slot: TimeTable): Boolean {
        if (slot.subjectId == null) {
            return true
        }
        val intent = createIntentForExactRTCAlarm(slot)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            createRequestCodeForExactRCTAlarm(slot.subjectId, slot.startTimeMillis),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent != null
    }
}