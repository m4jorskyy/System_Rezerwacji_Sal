package com.example.rezerwacje.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rezerwacje.data.database.ReservationsApp
import com.example.rezerwacje.data.database.ReservationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val repo = ReservationsRepository(NotificationScheduler(context))
        val dao = ReservationsApp.database.reservationsDao()

        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val all = dao.getUpcomingReservations(now)

            all.forEach { r ->
                when (r.alarmState) {
                    AlarmState.PENDING, AlarmState.SCHEDULED -> {
                        repo.scheduleForReservation(r.id)
                    }
                    AlarmState.CANCELED -> {}
                }
            }
        }
    }
}