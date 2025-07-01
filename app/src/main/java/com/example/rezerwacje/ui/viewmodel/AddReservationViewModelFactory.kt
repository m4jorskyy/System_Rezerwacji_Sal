package com.example.rezerwacje.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences

class AddReservationViewModelFactory(
    private val context: Context,
    private val localRepo: ReservationsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = AuthPreferences(context)
        return AddReservationViewModel(authPreferences, localRepo) as T
    }
}
