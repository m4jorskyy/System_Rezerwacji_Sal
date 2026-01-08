package com.example.rezerwacje.data.model

import com.squareup.moshi.JsonClass

data class GlobalStats(
    val weekStart: String,
    val totalReservations: Int,
    val totalHours: Double,
    val topRoomId: Int?,
    val topUserId: Int?
)