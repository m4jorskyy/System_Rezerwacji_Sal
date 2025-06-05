package com.example.rezerwacje.data.model

import java.time.LocalDateTime

data class RoomFilterRequest(
    val name: String? = null,
    val building: String? = null,
    val capacity: Int? = null,
    val floor: Int? = null,
    val hasBoard: Boolean? = null,
    val hasProjector: Boolean? = null,
    val hasDesks: Boolean? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null
)
