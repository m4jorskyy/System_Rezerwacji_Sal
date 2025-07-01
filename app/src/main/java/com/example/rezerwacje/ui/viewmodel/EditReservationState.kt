package com.example.rezerwacje.ui.viewmodel

sealed class EditReservationState {
    object Idle : EditReservationState()
    object Loading : EditReservationState()
    object Success : EditReservationState()
    object RoomsLoaded : EditReservationState()
    data class Error(val message: String) : EditReservationState()
    data class AlarmFailed(val msg: String): EditReservationState()
}