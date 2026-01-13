package com.example.rezerwacje.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleAuthRequest(
    val code: String
)