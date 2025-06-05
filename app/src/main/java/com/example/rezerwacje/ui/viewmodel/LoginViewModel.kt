package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun login(request: LoginRequest){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = RetrofitInstance.api.login(request)
                _loginState.value = LoginState.Success(response)
                _userRole.value = response.role
                _userName.value = response.username
                authPreferences.saveToken(response.token)
                authPreferences.saveUserId(response.id)
                authPreferences.saveUserRole(response.role)
                authPreferences.saveUserName(response.username)
            } catch(e: HttpException) {
                _loginState.value = LoginState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState(){
        _loginState.value = LoginState.Idle
    }
}