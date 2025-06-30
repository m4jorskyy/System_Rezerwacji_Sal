package com.example.rezerwacje.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rezerwacje.data.database.ReservationsApp
import com.example.rezerwacje.data.database.ReservationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId

class NotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dao = ReservationsApp.database.reservationsDao()
            val repo = ReservationsRepository()
            val scheduler = NotificationScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                val now = System.currentTimeMillis()
                val reservations = repo.getUpcomingReservations(now)
                reservations.forEach { item ->
                    val triggerAt = item.startTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli() - 10 * 60 * 1000L

                    if (triggerAt > now) {
                        scheduler.scheduleNotification(
                            id = item.id,
                            triggerAt = triggerAt,
                            title = "Przypomnienie: ${item.name}",
                            text = "Zdarzenie zaraz się zacznie"
                        )
                    }
                }
            }
        }
    }
}
