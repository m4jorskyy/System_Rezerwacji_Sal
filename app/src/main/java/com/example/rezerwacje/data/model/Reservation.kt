package com.example.rezerwacje.data.model

import java.time.LocalDateTime

data class Reservation(
    val id: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val createdAt: LocalDateTime,
    val title: String,
    val userId: Int,
    val roomId: Int,
    val roomName: String? = null
)