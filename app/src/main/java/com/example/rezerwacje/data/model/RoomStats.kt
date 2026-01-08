package com.example.rezerwacje.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomStats(
    val id: Int,
    val weekStart: String,
    val roomId: Int,
    val reservationsCount: Int,
    val totalHours: Double,
    val avgHours: Double
)