package com.example.rezerwacje.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rezerwacje.data.database.ReservationsRepository

class NotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repo = ReservationsRepository()
            val scheduler = NotificationScheduler(context)
            val viewModel = NotificationViewModel(repo, scheduler)

            viewModel.scheduleAll()
        }
    }

}