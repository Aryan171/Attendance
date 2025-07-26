package com.example.attendance.alarms.alarmReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.alarms.attendanceApp_periodicAlarm

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
             attendanceApp_periodicAlarm -> {
                Log.i("AlarmReceiver", "periodic alarm triggered")
            }

            attendanceApp_notificationAlarm -> {
                Log.i("AlarmReceiver", "notification alarm triggered")
            }
        }
    }
}