package com.example.rezerwacje.ui.viewmodel

sealed class AddReservationState {
    object Idle : AddReservationState()
    object Loading : AddReservationState()
    object RoomsLoaded : AddReservationState()
    object ReservationSuccess : AddReservationState()
    data class Error(val msg: String): AddReservationState()
}