package com.example.rezerwacje.data.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @ToJson
    fun toJson(value: LocalDateTime): String {
        return value.format(formatter)
    }

    @FromJson
    fun fromJson(value: String): LocalDateTime {
        return LocalDateTime.parse(value, formatter)
    }
}
