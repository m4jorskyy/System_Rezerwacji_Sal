package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel: ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun register(request: RegisterRequest){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val response = RetrofitInstance.api.register(request)
                _registerState.value = RegisterState.Success(response)
            } catch(e: HttpException){
                _registerState.value = RegisterState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState(){
        _registerState.value = RegisterState.Idle
    }
}