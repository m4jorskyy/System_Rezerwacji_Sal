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

class EditRoomViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _editRoomState = MutableStateFlow<EditRoomState>(EditRoomState.Idle)
    val editRoomState: StateFlow<EditRoomState> = _editRoomState

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun loadRoom(roomId: Int) {
        viewModelScope.launch {
            _editRoomState.value = EditRoomState.Loading
            try {
                val token = authPreferences.token.first()
                val room = RetrofitInstance.api.getRoomById(roomId, "Bearer $token")
                _editRoomState.value = EditRoomState.RoomLoaded(room)
            } catch (e: HttpException) {
                _editRoomState.value = EditRoomState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _editRoomState.value = EditRoomState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun editRoom(roomId: Int, request: AddRoomRequest) {
        viewModelScope.launch {
            _editRoomState.value = EditRoomState.Loading
            try {
                val token = authPreferences.token.first()
                RetrofitInstance.api.editRoom(roomId, request, "Bearer $token")
                _editRoomState.value = EditRoomState.Success("Room edited successfully")
            } catch (e: HttpException) {
                _editRoomState.value = EditRoomState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _editRoomState.value = EditRoomState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSubmit(
        roomId: Int,
        name: String,
        building: String,
        capacity: Int,
        floor: Int,
        whiteboard: Boolean,
        projector: Boolean,
        desks: Boolean
    ) {
        when {
            name.isBlank() -> {
                emitUiMessage("Name cannot be empty!")
                return
            }
            building.isBlank() -> {
                emitUiMessage("Building cannot be empty!")
                return
            }
            capacity <= 0 -> {
                emitUiMessage("Capacity must be greater than 0!")
                return
            }
            floor < 0 -> {
                emitUiMessage("Floor cannot be negative!")
                return
            }
            else -> {
                editRoom(
                    roomId,
                    AddRoomRequest(
                        name = name,
                        building = building,
                        capacity = capacity,
                        floor = floor,
                        whiteboard = whiteboard,
                        projector = projector,
                        desks = desks
                    )
                )
            }
        }
    }

    fun resetState() {
        _editRoomState.value = EditRoomState.Idle
    }
}
