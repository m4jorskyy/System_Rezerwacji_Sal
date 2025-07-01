package com.example.rezerwacje.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.notification.NotificationScheduler

class EditReservationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = AuthPreferences(context)
        val scheduler = NotificationScheduler(context)
        val repo = ReservationsRepository(scheduler)
        return EditReservationViewModel(authPreferences, repo) as T
    }
}
