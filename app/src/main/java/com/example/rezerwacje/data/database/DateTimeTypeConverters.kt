package com.example.rezerwacje.data.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object DateTimeTypeConverters {

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(millis: Long?): LocalDateTime? {
        return millis
            ?.let { Instant.ofEpochMilli(it) }
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDateTime()
    }
}
