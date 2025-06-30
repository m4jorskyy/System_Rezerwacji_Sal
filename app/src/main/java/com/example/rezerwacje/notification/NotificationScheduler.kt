package com.example.rezerwacje.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import java.util.Date
import kotlin.jvm.java

class NotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(id: Int, triggerAt: Long, title: String, text: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("Scheduler", "canScheduleExactAlarms: ${alarmManager.canScheduleExactAlarms()}")
            if (!(alarmManager.canScheduleExactAlarms())) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            }
        }

        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("text", text)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("Scheduler", "scheduling id=$id at=${Date(triggerAt)} now=${Date(System.currentTimeMillis())}")


        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }

    fun cancel(id: Int) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}