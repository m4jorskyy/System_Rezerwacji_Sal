package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddRoomRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddRoomViewModel(private val authPreferences: AuthPreferences): ViewModel() {
    private val _addRoomState = MutableStateFlow<AddRoomState>(AddRoomState.Idle)
    val addRoomState: StateFlow<AddRoomState> = _addRoomState

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun addRoom(request: AddRoomRequest) {
        viewModelScope.launch {
            _addRoomState.value = AddRoomState.Loading

            try {
                val token = authPreferences.token.first()
                val response = RetrofitInstance.api.addRoom(request, "Bearer $token")
                _addRoomState.value = AddRoomState.Success(response)

            } catch (e: HttpException) {
                _addRoomState.value = AddRoomState.Error("HTTP: ${e.code()}")

            } catch (e: Exception) {
                _addRoomState.value = AddRoomState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSubmit(name: String, building: String, capacity: String, floor: String, whiteboard: Boolean, projector: Boolean, desks: Boolean) {
        val capacity = capacity.toIntOrNull()
        val floor = floor.toIntOrNull()
        when {
            capacity == null -> emitUiMessage("Capacity must be a number!")
            floor == null -> emitUiMessage("Floor must be a number!")
            capacity <= 0 -> emitUiMessage("Capacity must be greater than 0!")
            floor <= 0 -> emitUiMessage("Floor must be greater than 0!")
            else -> {
                addRoom(
                    AddRoomRequest(
                        name = name,
                        building = building,
                        capacity = capacity.toInt(),
                        floor = floor.toInt(),
                        whiteboard = whiteboard,
                        projector = projector,
                        desks = desks
                    )
                )
            }
        }
    }

    fun resetState() {
        _addRoomState.value = AddRoomState.Idle
    }
}