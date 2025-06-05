package com.example.rezerwacje.data.model

data class RegisterResponse(
    val id: Int,
    val username: String,
    val lastname: String,
    val email: String,
    val role: String
)