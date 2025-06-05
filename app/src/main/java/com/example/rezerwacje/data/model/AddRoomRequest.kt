package com.example.rezerwacje.data.model

data class AddRoomRequest(
    val name: String,
    val building: String,
    val capacity: Int,
    val floor: Int,
    val whiteboard: Boolean,
    val projector: Boolean,
    val desks: Boolean
)