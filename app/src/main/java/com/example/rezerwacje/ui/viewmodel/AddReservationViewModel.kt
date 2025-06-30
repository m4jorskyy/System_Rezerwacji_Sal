package com.example.rezerwacje.ui.viewmodel

import android.database.sqlite.SQLiteException
import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import com.example.rezerwacje.data.model.toEntity
import com.example.rezerwacje.notification.NotificationScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class AddReservationViewModel(
    private val authPreferences: AuthPreferences,
    private val localRepo: ReservationsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    companion object {
        private const val OFFSET = 10 * 60 * 1_000L
    }

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
            val response = try {
                val token = authPreferences.token.first()
                val request = AddReservationRequest(roomId, userId, startTime, endTime, title)
                RetrofitInstance.api.addReservation(request, "Bearer $token")
            } catch (e: HttpException) {
                _addReservationState.value = AddReservationState.Error("HTTP: ${e.code()}")
                return@launch
            } catch (e: Exception) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
                return@launch
            }

            val entity = response.toEntity()
            try {
                localRepo.insertReservation(entity)
            } catch (e: SQLiteException) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
                return@launch
            } catch (e: Exception) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
                return@launch
            }

            try {
                notificationScheduler.scheduleNotification(
                    id        = entity.id,
                    triggerAt = entity.startTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli() - OFFSET,
                    title     = "Przypomnienie: ${entity.name}",
                    text      = "Zdarzenie zaraz się zacznie"
                )
            } catch (e: SecurityException) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
                return@launch
            } catch (e: Exception) {
                _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
                return@launch
            }

            _addReservationState.value = AddReservationState.ReservationSuccess

        }
    }

    fun onSubmit(
        roomId: Int?,
        userId: Int,
        startDateTime: LocalDateTime?,
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