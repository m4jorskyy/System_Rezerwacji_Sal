package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ViewReservationsViewModel(
    private val authPreferences: AuthPreferences,
    private val localRepo: ReservationsRepository
) : ViewModel() {
    private val _reservationsState =
        MutableStateFlow<ViewReservationsState>(ViewReservationsState.Idle)
    val reservationsState: StateFlow<ViewReservationsState> = _reservationsState

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun getUserReservations() {
        viewModelScope.launch {
            _reservationsState.value = ViewReservationsState.Loading

            try {
                val token = authPreferences.token.first()
                val userId = authPreferences.userId.first()
                val response = RetrofitInstance.api.getUserReservations(userId, "Bearer $token")
                _reservationsState.value = ViewReservationsState.Success(response)

            } catch (e: HttpException) {
                _reservationsState.value = ViewReservationsState.Error("HTTP: ${e.code()}\n ${e.message()} ")
            } catch (e: Exception) {
                _reservationsState.value = ViewReservationsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteReservation(reservationId: Int) {
        viewModelScope.launch {
            try {
                val token = authPreferences.token.first()
                val response = RetrofitInstance.api.deleteReservation(reservationId, "Bearer $token")
                if (response.isSuccessful) {
                    localRepo.cancelAlarm(reservationId)
                    localRepo.deleteReservation(reservationId)
                    getUserReservations()
                }
            } catch (e: HttpException) {
                _reservationsState.value = ViewReservationsState.Error("HTTP: ${e.code()}")
                return@launch
            } catch (e: Exception) {
                _reservationsState.value = ViewReservationsState.Error(e.message ?: "Unknown error")
                return@launch
            }
        }
    }

    fun resetState() {
        _reservationsState.value = ViewReservationsState.Idle
    }
}