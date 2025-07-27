package com.example.attendance.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.attendance.R
import com.example.attendance.alarms.broadcastReceiver.AttendanceBroadcastReceiver
import com.example.attendance.alarms.attendanceApp_markAbsent
import com.example.attendance.alarms.attendanceApp_markPresent
import com.example.attendance.database.DatabaseRepository
import com.example.attendance.database.timeTable.TimeTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Notification(
    private val context: Context,
    private val db: DatabaseRepository
) {
    companion object {
        const val CHANNEL_ID = "attendance_app_notification_channel"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "attendance notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notification which allows you to set present or absent for your classes"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(slotId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            val slot = withContext(Dispatchers.IO) {
                db.getSlotById(slotId)
            }
            if (slot == null || slot.subjectId == null) {
                return@launch
            }

            val subject = withContext(Dispatchers.IO) {
                db.getSubjectById(slot.subjectId)
            }

            if (subject == null) {
                return@launch
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(subject.name)
                .setContentText("Mark attendance for today")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.checkboxfilled)
                .addAction(R.drawable.check, "Present", markPresentPendingIntent(slot))
                .addAction(R.drawable.close, "Absent", markAbsentPendingIntent(slot))

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@with
                }

                notify(notificationId(slot), builder.build())
            }
        }
    }

    fun markPresentPendingIntent(slot: TimeTable): PendingIntent {
        val intent = Intent(context, AttendanceBroadcastReceiver::class.java)
            .setAction(attendanceApp_markPresent)
            .putExtra("slotId", slot.id)
            .putExtra("notificationId", notificationId(slot))

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slot.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent
    }

    fun markAbsentPendingIntent(slot: TimeTable): PendingIntent {
        val intent = Intent(context, AttendanceBroadcastReceiver::class.java)
            .setAction(attendanceApp_markAbsent)
            .putExtra("slotId", slot.id)
            .putExtra("notificationIdId", notificationId(slot))

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slot.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent
    }

    private fun notificationId(slot: TimeTable): Int = slot.id.hashCode()
}