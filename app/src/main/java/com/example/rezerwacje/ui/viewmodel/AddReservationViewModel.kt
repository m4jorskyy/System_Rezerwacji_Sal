package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import com.example.rezerwacje.data.model.toEntity
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime

@OptIn(FlowPreview::class)
class AddReservationViewModel(
    private val authPreferences: AuthPreferences,
    private val localRepo: ReservationsRepository,
) : ViewModel() {

    private val _addReservationState =
        MutableStateFlow<AddReservationState>(AddReservationState.Idle)
    val addReservationState: StateFlow<AddReservationState> = _addReservationState

    private val _rooms = MutableStateFlow<List<RoomDataModel>>(emptyList())
    val rooms: StateFlow<List<RoomDataModel>> = _rooms

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    // --- STANY FILTRÓW ---
    private val _startDateTime = MutableStateFlow<LocalDateTime?>(null)
    val startDateTime = _startDateTime.asStateFlow()

    private val _endDateTime = MutableStateFlow<LocalDateTime?>(null)
    val endDateTime = _endDateTime.asStateFlow()

    private val _capacity = MutableStateFlow("")
    val capacity = _capacity.asStateFlow()

    private val _hasWhiteboard = MutableStateFlow(false)
    val hasWhiteboard = _hasWhiteboard.asStateFlow()

    private val _hasProjector = MutableStateFlow(false)
    val hasProjector = _hasProjector.asStateFlow()

    private val _hasDesks = MutableStateFlow(false)
    val hasDesks = _hasDesks.asStateFlow()
    // ---------------------

    init {
        // AUTOMATYCZNE WYSZUKIWANIE
        viewModelScope.launch {
            combine(
                _startDateTime,
                _endDateTime,
                _capacity,
                _hasWhiteboard,
                _hasProjector,
                _hasDesks
            ) { args: Array<Any?>->
                // Przy 6+ argumentach otrzymujemy tablicę (Array), musimy rzutować ręcznie
                val start = args[0] as LocalDateTime?
                val end = args[1] as LocalDateTime?
                val capStr = args[2] as String
                val wb = args[3] as Boolean
                val proj = args[4] as Boolean
                val desks = args[5] as Boolean

                // Tworzymy obiekt requestu TYLKO jeśli daty są wybrane
                if (start != null && end != null) {
                    val capacityInt = capStr.toIntOrNull()
                    RoomFilterRequest(
                        startTime = start,
                        endTime = end,
                        capacity = capacityInt,
                        hasBoard = wb,
                        hasProjector = proj,
                        hasDesks = desks
                    )
                } else {
                    null // Jeśli brak dat, nie szukamy
                }
            }
                .debounce(1000L) // Czekamy 1000ms na brak zmian (jak w React)
                .collectLatest { request ->
                    if (request != null) {
                        // Sprawdzenie poprawności dat przed wysłaniem
                        if (request.startTime != null && request.endTime != null &&
                            request.endTime.isAfter(request.startTime)) {
                            fetchRooms(request)
                        }
                    }
                }
        }
    }

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    // Settery dla widoku
    fun onStartTimeChange(date: LocalDateTime) { _startDateTime.value = date }
    fun onEndTimeChange(date: LocalDateTime) { _endDateTime.value = date }
    fun onCapacityChange(value: String) {
        if (value.all { it.isDigit() }) { _capacity.value = value }
    }
    fun onWhiteboardChange(isChecked: Boolean) { _hasWhiteboard.value = isChecked }
    fun onProjectorChange(isChecked: Boolean) { _hasProjector.value = isChecked }
    fun onDesksChange(isChecked: Boolean) { _hasDesks.value = isChecked }


    private suspend fun fetchRooms(request: RoomFilterRequest) {
        _addReservationState.value = AddReservationState.Loading
        try {
            val token = authPreferences.token.first()
            val response = RetrofitInstance.api.filterRooms(request, "Bearer $token")
            _rooms.value = response
            _addReservationState.value = AddReservationState.RoomsLoaded
        } catch (e: HttpException) {
            _addReservationState.value = AddReservationState.Error("HTTP: ${e.code()}")
        } catch (e: Exception) {
            _addReservationState.value = AddReservationState.Error(e.message ?: "Unknown error")
        }
    }

    // Dodawanie rezerwacji (Submit)
    fun onSubmit(
        roomId: Int?,
        userId: Int,
        title: String
    ) {
        val start = _startDateTime.value
        val end = _endDateTime.value

        if (start == null) {
            emitUiMessage("Please select start date & time!")
            return
        }
        if (end == null) {
            emitUiMessage("Please select end date & time!")
            return
        }
        if (!end.isAfter(start)) {
            emitUiMessage("End must be after start!")
            return
        }
        if (start.isBefore(LocalDateTime.now())) {
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

        addReservationInternal(
            roomId = roomId,
            userId = userId,
            startTime = start,
            endTime   = end,
            title     = title
        )
    }

    private fun addReservationInternal(
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
                localRepo.scheduleForReservation(entity.id)
            } catch (e: Exception) {
                // Log error but success flow continues
            }

            _addReservationState.value = AddReservationState.ReservationSuccess
        }
    }

    fun resetState() {
        _addReservationState.value = AddReservationState.Idle
    }
}