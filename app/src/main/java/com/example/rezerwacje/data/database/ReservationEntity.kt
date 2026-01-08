package com.example.rezerwacje.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rezerwacje.notification.AlarmState
import java.time.LocalDateTime

@Entity(tableName = "reservations")
@TypeConverters(DateTimeTypeConverters::class)
data class ReservationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val nextTriggerAt: Long? = null,
    val alarmState: AlarmState = AlarmState.PENDING,
    val roomId: Int
)