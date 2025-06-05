package com.example.rezerwacje.data.model

data class RegisterRequest(
    val username: String,
    val lastname: String,
    val email: String,
    val password: String
)