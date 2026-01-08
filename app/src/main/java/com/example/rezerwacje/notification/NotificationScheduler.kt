package com.example.rezerwacje.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import java.util.Date
import androidx.core.net.toUri

class NotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(id: Int, triggerAt: Long, title: String, text: String) {
        // Tworzymy Intent z unikalnym DATA, żeby isAlarmActive działało poprawnie
        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("text", text)
            // WAŻNE: Dodajemy data, żeby PendingIntent był unikalny i zgodny z isAlarmActive
            data = "reservation://$id".toUri()
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("Scheduler", "Planowanie ID=$id na czas=${Date(triggerAt)}")

        // Logika wyboru typu alarmu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                // Mamy uprawnienia -> Dokładny alarm
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            } else {
                // BRAK uprawnień -> Fallback do mniej dokładnego alarmu zamiast return!
                Log.w("Scheduler", "Brak uprawnień do dokładnych alarmów. Używam setAndAllowWhileIdle.")
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            }
        } else {
            // Starszy Android -> Zawsze dokładny
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        }
    }

    fun isAlarmActive(id: Int): Boolean {
        // Intent musi być IDENTYCZNY jak przy tworzeniu (dlatego dodaliśmy data wyżej)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            data = "reservation://$id".toUri()
        }
        val pi = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pi != null
    }

    fun cancelNotification(id: Int) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            data = "reservation://$id".toUri()
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("Scheduler", "Anulowano alarm ID=$id")
    }
}