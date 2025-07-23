package com.example.attendance.alarms.alarmSchedurer

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.example.attendance.alarms.alarmReceiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleAlarm() {
        val pendingIntent = Intent(context, AlarmReceiver::class.java)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 10000,
            PendingIntent.getBroadcast(
                context,
                0,
                pendingIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}