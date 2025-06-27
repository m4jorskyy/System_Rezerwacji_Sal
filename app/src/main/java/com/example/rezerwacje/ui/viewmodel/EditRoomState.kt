package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.RoomDataModel

sealed class EditRoomState {
    object Idle : EditRoomState()
    object Loading : EditRoomState()
    data class RoomLoaded(val room: RoomDataModel) : EditRoomState()
    data class Success(val message: String) : EditRoomState()
    data class Error(val message: String) : EditRoomState()
}