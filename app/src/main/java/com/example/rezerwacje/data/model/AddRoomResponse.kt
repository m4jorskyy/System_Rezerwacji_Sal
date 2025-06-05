package com.example.rezerwacje.data.model

data class AddRoomResponse(
    val id: Int,
    val name: String,
    val building: String,
    val capacity: Int,
    val floor: Int,
    val whiteboard: Boolean,
    val projector: Boolean,
    val desks: Boolean
)
