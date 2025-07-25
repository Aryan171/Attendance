package com.example.attendance.alarms.alarmSchedurer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.attendance.alarms.PERIODIC_ALARM
import com.example.attendance.alarms.alarmReceiver.AlarmReceiver
import com.example.attendance.alarms.pendingIntentRequestCodes.PERIODIC_ALARM_REQUEST_CODE

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedulePeriodicAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("alarmType", PERIODIC_ALARM)

        if (isAlarmScheduled(intent)) {
            return;
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
            AlarmManager.INTERVAL_DAY * 2,
            pendingIntent
        )
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