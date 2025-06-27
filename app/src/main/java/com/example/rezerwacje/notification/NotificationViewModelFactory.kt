package com.example.rezerwacje.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rezerwacje.data.database.ReservationsRepository

class NotificationViewModelFactory(
    private val repo: ReservationsRepository,
    private val scheduler: NotificationScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(repo, scheduler) as T
    }
}
