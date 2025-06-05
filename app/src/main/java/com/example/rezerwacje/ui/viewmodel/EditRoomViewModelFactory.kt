package com.example.rezerwacje.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rezerwacje.data.local.AuthPreferences

class EditRoomViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = AuthPreferences(context)
        return EditRoomViewModel(authPreferences) as T
    }
}