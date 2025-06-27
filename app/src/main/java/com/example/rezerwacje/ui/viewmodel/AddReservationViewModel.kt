package com.example.rezerwacje.ui.viewmodel

import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AddReservationViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _addReservationState =
        MutableStateFlow<AddReservationState>(AddReservationState.Idle)
    val addReservationState: StateFlow<AddReservationState> = _addReservationState

    private val _rooms = MutableStateFlow<List<RoomDataModel>>(emptyList())
    val rooms: StateFlow<List<RoomDataModel>> = _rooms

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun findAvailableRooms(startTime: LocalDateTime, endTime: LocalDateTime) {
        viewModelScope.launch {
            _addReservationState.value = AddReservationState.Loading

            try {
                val token = authPreferences.token.first()
                val roomRequest = RoomFilterRequest(startTime = startTime, endTime = endTime)
                val response = RetrofitInstance.api.filterRooms(roomRequest, "Bearer $token")
                _rooms.value = response
                _addReservationState.value = AddReservationState.RoomsLoaded

            } catch (e: HttpException) {
                _addReservationState.value = AddReservationState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addReservation(
        roomId: Int,
        userId: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            _addReservationState.value = AddReservationState.Loading
            try {
                val token = authPreferences.token.first()
                val request = AddReservationRequest(roomId, userId, startTime, endTime, title)
                RetrofitInstance.api.addReservation(request, "Bearer $token")
                _addReservationState.value = AddReservationState.ReservationSuccess
            } catch (e: HttpException) {
                _addReservationState.value = AddReservationState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSubmit(
        roomId: Int?,
        userId: Int,
        startDateTime: LocalDateTime?,   // teraz pełne daty
        endDateTime: LocalDateTime?,
        title: String
    ) {
        if (startDateTime == null) {
            emitUiMessage("Please select start date & time!")
            return
        }
        if (endDateTime == null) {
            emitUiMessage("Please select end date & time!")
            return
        }
        if (!endDateTime.isAfter(startDateTime)) {
            emitUiMessage("End must be after start!")
            return
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            emitUiMessage("Cannot make reservation in the past!")
            return
        }
        if (roomId == null) {
            emitUiMessage("Please select a room!")
            return
        }
        if (title.isBlank()) {
            emitUiMessage("Title cannot be empty!")
            return
        }

        addReservation(
            roomId = roomId,
            userId = userId,
            startTime = startDateTime,
            endTime   = endDateTime,
            title     = title
        )
    }

    fun resetState() {
        _addReservationState.value = AddReservationState.Idle
    }

}