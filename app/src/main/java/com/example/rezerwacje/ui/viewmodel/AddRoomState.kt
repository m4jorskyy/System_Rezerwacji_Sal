package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.AddRoomResponse

sealed class AddRoomState {
    object Idle : AddRoomState()
    object Loading : AddRoomState()
    data class Success(val response: AddRoomResponse) : AddRoomState()
    data class Error(val message: String) : AddRoomState()

}