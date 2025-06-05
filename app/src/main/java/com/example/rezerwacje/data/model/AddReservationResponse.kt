package com.example.rezerwacje.data.model

import java.time.LocalDateTime

data class AddReservationResponse(
    val id: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String,
    val userId: Int,
    val roomId: Int
)
