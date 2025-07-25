package com.example.attendance.alarms.alarmReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.attendance.alarms.attendanceApp_notificationAlarm
import com.example.attendance.alarms.attendanceApp_periodicAlarm

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("AlarmReceiver", "Alarm received")
        val alarmType = intent?.getStringExtra("alarmType") ?: ""

        Log.i("AlarmReceiver", "Alarm type: \"$alarmType\"")

        when (alarmType) {
             attendanceApp_periodicAlarm -> {
                println("periodic alarm triggered")
            }

            attendanceApp_notificationAlarm -> {
                println("notification alarm triggered for ${intent?.getStringExtra("subject")}")
            }
        }
    }
}