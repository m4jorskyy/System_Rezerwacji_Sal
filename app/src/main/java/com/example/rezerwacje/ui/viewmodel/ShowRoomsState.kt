package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.RoomDataModel

sealed class ShowRoomsState {
    object Idle: ShowRoomsState()
    object Loading: ShowRoomsState()
    data class Success(val rooms: List<RoomDataModel>): ShowRoomsState()
    data class Error(val message: String): ShowRoomsState()
}
