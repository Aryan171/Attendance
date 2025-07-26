package com.example.attendance.alarms.alarmSchedurer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.attendance.alarms.alarmReceiver.AlarmReceiver
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.alarms.attendanceApp_periodicAlarm
import com.example.attendance.alarms.pendingIntentRequestCodes.PERIODIC_ALARM_REQUEST_CODE

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedulePeriodicAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.action = attendanceApp_periodicAlarm

        if (isAlarmScheduled(intent)) {
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            PERIODIC_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // suppressing the missing permission because we are using USE_EXACT_ALARM permission
    // which is granted by default
    @SuppressLint("MissingPermission")
    fun scheduleExactRTCAlarm(subjectId: Long, epochTimeMillis: Long) {
        val intent = createIntentForExactRTCAlarm(subjectId)

        if (isAlarmScheduled(intent)) {
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            subjectId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            epochTimeMillis,
            pendingIntent
        )
    }

    fun cancelExactRTCAlarm(subjectId: Long) {
        val intent = createIntentForExactRTCAlarm(subjectId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            subjectId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    fun createIntentForExactRTCAlarm(subjectId: Long): Intent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = attendanceApp_notificationAlarm
        intent.putExtra("subjectId", subjectId)

        return intent
    }

    fun isAlarmScheduled(intent: Intent): Boolean {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            PERIODIC_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        return pendingIntent != null
    }
}