package com.example.rezerwacje.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.database.ReservationsRepository
import kotlinx.coroutines.launch
import java.time.ZoneId

class NotificationViewModel(
    private val repo: ReservationsRepository,
    private val scheduler: NotificationScheduler
) : ViewModel() {

    fun scheduleAll() = viewModelScope.launch {
        val now = System.currentTimeMillis()
        val items = repo.getUpcomingReservations(now)
        Log.d("NotifyVM", "scheduleAll() wywołane, upcoming=${items.size}")
        items.forEach { item ->
            val startEpoch = item.startTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val triggerAt = startEpoch - 10 * 60 * 1000L

            if(triggerAt > now) {
                try {
                    scheduler.scheduleNotification(
                        id        = item.id,
                        triggerAt = triggerAt,
                        title     = "Przypomnienie: ${item.name}",
                        text      = "Zdarzenie zaraz się zacznie"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    fun cancel(id: Int) {
        scheduler.cancel(id)
    }
}
