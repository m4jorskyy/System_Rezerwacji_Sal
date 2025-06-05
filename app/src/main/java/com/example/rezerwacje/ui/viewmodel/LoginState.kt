package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.LoginResponse

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}