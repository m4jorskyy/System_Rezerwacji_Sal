package com.example.rezerwacje.data.model

import java.time.LocalDateTime

data class AddReservationRequest(
    val roomId: Int,
    val userId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String
)
