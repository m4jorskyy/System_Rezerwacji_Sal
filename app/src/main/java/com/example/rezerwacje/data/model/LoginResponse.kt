package com.example.rezerwacje.data.model

data class LoginResponse(
    val id: Int,
    val token: String,
    val username: String,
    val lastname: String,
    val email: String,
    val role: String
)
