package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.rezerwacje.data.model.Reservation
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDateTime

class EditReservationViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _reservation = MutableStateFlow<Reservation?>(null)
    val reservation: StateFlow<Reservation?> = _reservation

    private val _editReservationState = MutableStateFlow<EditReservationState>(EditReservationState.Idle)
    val editReservationState: StateFlow<EditReservationState> = _editReservationState

    private val _rooms = MutableStateFlow<List<RoomDataModel>>(emptyList())
    val rooms: StateFlow<List<RoomDataModel>> = _rooms

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun editReservation(reservationId: Int, request: AddReservationRequest) {
        viewModelScope.launch {
            _editReservationState.value = EditReservationState.Loading
            try {
                val token = authPreferences.token.first()
                RetrofitInstance.api.editReservation(reservationId, request, "Bearer $token")
                _editReservationState.value = EditReservationState.Success
            } catch (e: HttpException) {
                _editReservationState.value = EditReservationState.Error("HTTP ${e.code()}")
            } catch (e: Exception) {
                _editReservationState.value = EditReservationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadReservation(reservationId: Int) {
        viewModelScope.launch {
            _editReservationState.value = EditReservationState.Loading
            try {
                val token = authPreferences.token.first()
                val reservations = RetrofitInstance.api.getUserReservations(
                    userId = authPreferences.userId.first(),
                    token = "Bearer $token"
                )
                val reservation = reservations.find { it.id == reservationId }
                if (reservation == null) {
                    emitUiMessage("Reservation not found")
                    _editReservationState.value = EditReservationState.Error("Reservation not found")
                    return@launch
                }
                _reservation.value = reservation
                _editReservationState.value = EditReservationState.Idle
            } catch (e: HttpException) {
                _editReservationState.value = EditReservationState.Error("HTTP ${e.code()}")
            } catch (e: Exception) {
                _editReservationState.value = EditReservationState.Error(e.message ?: "Error")
            }
        }
    }

    fun findAvailableRooms(startTime: LocalDateTime, endTime: LocalDateTime) {
        if (!endTime.isAfter(startTime)) {
            emitUiMessage("End time must be after start time!")
            return
        }

        viewModelScope.launch {
            _editReservationState.value = EditReservationState.Loading
            try {
                val token = authPreferences.token.first()
                val roomRequest = RoomFilterRequest(startTime = startTime, endTime = endTime)
                val response = RetrofitInstance.api.filterRooms(roomRequest, "Bearer $token")
                _rooms.value = response
                _editReservationState.value = EditReservationState.RoomsLoaded
            } catch (e: HttpException) {
                _editReservationState.value = EditReservationState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _editReservationState.value = EditReservationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSubmit(
        reservationId: Int,
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

        editReservation(
            reservationId,
            AddReservationRequest(
                roomId = roomId,
                userId = userId,
                startTime = startDateTime,
                endTime = endDateTime,
                title = title
            )
        )
    }

    fun resetState() {
        _editReservationState.value = EditReservationState.Idle
    }
}