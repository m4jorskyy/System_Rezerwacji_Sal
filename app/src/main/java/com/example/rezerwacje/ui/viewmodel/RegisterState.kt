package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.RegisterResponse

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val response: RegisterResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}