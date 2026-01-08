package com.example.rezerwacje.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.database.ReservationsApp
import com.example.rezerwacje.data.database.ReservationsDao
import com.example.rezerwacje.data.database.ReservationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId

class NotificationViewModel(
    private val repo: ReservationsRepository,
    private val scheduler: NotificationScheduler,
    private val dao: ReservationsDao = ReservationsApp.database.reservationsDao()
) : ViewModel() {

    fun scheduleAll() = viewModelScope.launch {
        val now = System.currentTimeMillis()
        val items = repo.getUpcomingReservations(now)
        Log.d("NotifyVM", "scheduleAll() wywołane, upcoming=${items.size}")
        items.forEach { repo.scheduleForReservation(it.id) }
    }

    fun reconcileAlarms() = viewModelScope.launch(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        dao.getUpcomingReservations(now).forEach { r ->
            when (r.alarmState) {
                AlarmState.PENDING -> {
                    val nextTrigger = r.startTime.atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli() - 10 * 60 * 1000L

                    if (nextTrigger > now) {
                        scheduler.scheduleNotification(
                            id = r.id,
                            triggerAt = nextTrigger,
                            title = "Przypomnienie: ${r.title}",
                            text = "Zdarzenie zaraz się zacznie"
                        )

                        repo.scheduleForReservation(r.id)
                    }
                }

                AlarmState.SCHEDULED -> {
                    if (!scheduler.isAlarmActive(r.id)) {
                        scheduler.scheduleNotification(
                            id = r.id,
                            triggerAt = r.nextTriggerAt!!,
                            title = "Przypomnienie: ${r.title}",
                            text = "Zdarzenie zaraz się zacznie"
                        )
                        repo.scheduleForReservation(r.id)
                    }
                }

                AlarmState.CANCELED -> {}
            }
        }
    }

    fun cancel(id: Int) {
        scheduler.cancelNotification(id)
    }
}

