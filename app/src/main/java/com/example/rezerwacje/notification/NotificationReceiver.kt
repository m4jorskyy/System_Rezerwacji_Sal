package com.example.rezerwacje.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.rezerwacje.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "reservations_channel"
        private const val CHANNEL_NAME = "Rezerwacje"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action != null && action != Intent.ACTION_BOOT_COMPLETED) {
            Log.d("Receiver", "Received unknown or no action: $action")
            return
        }

        Log.d("Receiver", "onReceive id=${intent.getIntExtra("id", -1)}")
        val title = intent.getStringExtra("title") ?: "Rezerwacja"
        val text  = intent.getStringExtra("text")  ?: ""
        val id    = intent.getIntExtra("id", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Powiadomienia o nadchodzących rezerwacjach"
        }
        notificationManager.createNotificationChannel(channel)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reservation_id", id)
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            id,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        Log.d("Receiver", "dispatching notification for id=$id")

        notificationManager.notify(id, notification)
    }
}
