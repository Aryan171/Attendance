package com.example.attendance.alarms.alarmReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.attendance.alarms.PERIODIC_ALARM

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("AlarmReceiver", "Alarm received")
        val alarmType = intent?.getStringExtra("alarmType") ?: ""

        Log.i("AlarmReceiver", "Alarm type: \"$alarmType\"")

        when (alarmType) {
             PERIODIC_ALARM -> {
                println("periodic alarm triggered")
            }
        }
    }
}