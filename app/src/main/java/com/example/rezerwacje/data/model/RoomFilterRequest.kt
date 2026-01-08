package com.example.rezerwacje.data.model

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class RoomFilterRequest(
    val name: String? = null,
    val building: String? = null,
    val capacity: Int? = null,
    val floor: Int? = null,

    @Json(name = "whiteboard")
    val hasBoard: Boolean? = null,

    @Json(name = "projector")
    val hasProjector: Boolean? = null,

    @Json(name = "desks")
    val hasDesks: Boolean? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null
)
