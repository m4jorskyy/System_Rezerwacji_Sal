package com.example.rezerwacje.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStats(
    val id: Int,
    val weekStart: String,
    val userId: Int,
    val reservationsCount: Int,
    val totalHours: Double,
    val avgHours: Double
)