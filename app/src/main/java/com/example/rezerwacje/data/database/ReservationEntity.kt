package com.example.rezerwacje.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Entity(tableName = "reservations")
@TypeConverters(DateTimeTypeConverters::class)
data class ReservationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)