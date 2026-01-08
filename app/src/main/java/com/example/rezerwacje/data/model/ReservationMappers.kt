package com.example.rezerwacje.data.model

import com.example.rezerwacje.data.database.ReservationEntity
import java.time.LocalDateTime

fun AddReservationResponse.toEntity(): ReservationEntity {
    return ReservationEntity(
        id        = this.id,
        title      = this.title,
        startTime = LocalDateTime.parse(this.startTime.toString()),
        endTime   = LocalDateTime.parse(this.endTime.toString()),
        roomId = this.roomId
    )
}