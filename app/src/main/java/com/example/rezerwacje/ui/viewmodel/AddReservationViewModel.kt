package com.example.rezerwacje.ui.viewmodel

import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.Room
import com.example.rezerwacje.data.model.RoomFilterRequest
import com.example.rezerwacje.ui.screen.AddReservation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddReservationViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _addReservationState =
        MutableStateFlow<AddReservationState>(AddReservationState.Idle)
    val addReservationState: StateFlow<AddReservationState> = _addReservationState

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

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
        date: LocalDate?,
        startTimeStr: String?,
        endTimeStr: String?,
        title: String
    ) {
        if (date == null) {
            emitUiMessage("Please select a date!")
            return
        }

        val startTime = try {
            startTimeStr?.let { LocalTime.parse(it) }
        } catch (e: Exception) {
            null
        }

        if (startTime == null) {
            emitUiMessage("Invalid start time!")
            return
        }

        val endTime = try {
            endTimeStr?.let { LocalTime.parse(it) }
        } catch (e: Exception) {
            null
        }

        if (endTime == null) {
            emitUiMessage("Invalid end time!")
            return
        }

        if (!endTime.isAfter(startTime)) {
            emitUiMessage("End time must be after start time!")
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

        val startDateTime = date.atTime(startTime)
        val endDateTime = date.atTime(endTime)

        if (startDateTime.isBefore(LocalDateTime.now())) {
            emitUiMessage("Cannot make a reservation in the past!")
            return
        }

        addReservation(
            roomId = roomId,
            userId = userId,
            startTime = startDateTime,
            endTime = endDateTime,
            title = title
        )
    }

    fun resetState() {
        _addReservationState.value = AddReservationState.Idle
    }

}