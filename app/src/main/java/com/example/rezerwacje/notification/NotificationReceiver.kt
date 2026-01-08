package com.example.rezerwacje.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.rezerwacje.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "reservations_channel"
        private const val CHANNEL_NAME = "Rezerwacje"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Usunąłem sprawdzenie akcji - jest zbędne i może blokować alarmy

        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Rezerwacja"
        val text = intent.getStringExtra("text") ?: "Masz nadchodzącą rezerwację"

        Log.d("Receiver", "Odebrano alarm: id=$id, title=$title")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tworzenie kanału (wymagane od Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o nadchodzących rezerwacjach"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Kliknięcie w powiadomienie
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

        // Budowanie powiadomienia
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Upewnij się, że masz jakąś ikonę
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(id, notification)
    }
}