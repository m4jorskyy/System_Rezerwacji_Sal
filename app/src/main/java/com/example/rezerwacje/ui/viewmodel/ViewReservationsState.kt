package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.Reservation

sealed class ViewReservationsState {
    object Idle: ViewReservationsState()
    object Loading: ViewReservationsState()
    object RoomReservationsLoaded: ViewReservationsState()
    data class Success(val reservations: List<Reservation>): ViewReservationsState()
    data class Error(val message: String): ViewReservationsState()
}