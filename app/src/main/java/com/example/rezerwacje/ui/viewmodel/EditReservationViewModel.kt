package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.Reservation
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import com.example.rezerwacje.data.model.toEntity
import com.example.rezerwacje.notification.AlarmState
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
class EditReservationViewModel(
    private val authPreferences: AuthPreferences,
    private val localRepo: ReservationsRepository
) : ViewModel() {

    private val _editReservationState =
        MutableStateFlow<EditReservationState>(EditReservationState.Idle)
    val editReservationState: StateFlow<EditReservationState> = _editReservationState

    private val _rooms = MutableStateFlow<List<RoomDataModel>>(emptyList())
    val rooms: StateFlow<List<RoomDataModel>> = _rooms

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    // --- STANY FORMULARZA ---
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _selectedRoomId = MutableStateFlow<Int?>(null)
    val selectedRoomId = _selectedRoomId.asStateFlow()

    private val _startDateTime = MutableStateFlow<LocalDateTime?>(null)
    val startDateTime = _startDateTime.asStateFlow()

    private val _endDateTime = MutableStateFlow<LocalDateTime?>(null)
    val endDateTime = _endDateTime.asStateFlow()

    // --- FILTRY ---
    private val _capacity = MutableStateFlow("")
    val capacity = _capacity.asStateFlow()

    private val _hasWhiteboard = MutableStateFlow(false)
    val hasWhiteboard = _hasWhiteboard.asStateFlow()

    private val _hasProjector = MutableStateFlow(false)
    val hasProjector = _hasProjector.asStateFlow()

    private val _hasDesks = MutableStateFlow(false)
    val hasDesks = _hasDesks.asStateFlow()

    init {
        // AUTOMATYCZNE WYSZUKIWANIE SAL (DEBOUNCE)
        viewModelScope.launch {
            combine(
                _startDateTime,
                _endDateTime,
                _capacity,
                _hasWhiteboard,
                _hasProjector,
                _hasDesks
            ) { args: Array<Any?> ->
                val start = args[0] as LocalDateTime?
                val end = args[1] as LocalDateTime?
                val capStr = args[2] as String
                val wb = args[3] as Boolean
                val proj = args[4] as Boolean
                val desks = args[5] as Boolean

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
                    null
                }
            }
                .debounce(1000L)
                .collectLatest { request ->
                    if (request != null) {
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

    // --- SETTERY DLA UI ---
    fun onTitleChange(newTitle: String) { _title.value = newTitle }
    fun onRoomSelected(id: Int) { _selectedRoomId.value = id }
    fun onStartTimeChange(date: LocalDateTime) { _startDateTime.value = date }
    fun onEndTimeChange(date: LocalDateTime) { _endDateTime.value = date }
    fun onCapacityChange(value: String) {
        if (value.all { it.isDigit() }) { _capacity.value = value }
    }
    fun onWhiteboardChange(isChecked: Boolean) { _hasWhiteboard.value = isChecked }
    fun onProjectorChange(isChecked: Boolean) { _hasProjector.value = isChecked }
    fun onDesksChange(isChecked: Boolean) { _hasDesks.value = isChecked }

    // Ładowanie istniejącej rezerwacji i wypełnianie formularza
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

                // Wypełniamy stany danymi z pobranej rezerwacji
                _title.value = reservation.title
                _startDateTime.value = reservation.startTime
                _endDateTime.value = reservation.endTime
                _selectedRoomId.value = reservation.roomId

                // Filtry zostawiamy domyślne (false/empty), bo nie wiemy jakie wyposażenie ma obecna sala,
                // a użytkownik może chcieć wyszukać zupełnie inną.

                // Po ustawieniu dat, `combine` automatycznie odpali `fetchRooms`

                _editReservationState.value = EditReservationState.Idle
            } catch (e: HttpException) {
                _editReservationState.value = EditReservationState.Error("HTTP ${e.code()}")
            } catch (e: Exception) {
                _editReservationState.value = EditReservationState.Error(e.message ?: "Error")
            }
        }
    }

    private suspend fun fetchRooms(request: RoomFilterRequest) {
        // Nie zmieniamy stanu na Loading całego ekranu, żeby nie blokować UI podczas pisania
        // Można ewentualnie dodać osobny stan "isSearching"
        try {
            val token = authPreferences.token.first()
            val response = RetrofitInstance.api.filterRooms(request, "Bearer $token")
            _rooms.value = response
            _editReservationState.value = EditReservationState.RoomsLoaded
        } catch (e: HttpException) {
            // Ciche błędy przy filtrowaniu, ewentualnie log
        } catch (e: Exception) {
            // Ciche błędy
        }
    }

    fun onSubmit(reservationId: Int, userId: Int) {
        val start = _startDateTime.value
        val end = _endDateTime.value
        val roomId = _selectedRoomId.value
        val titleVal = _title.value

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
            emitUiMessage("Cannot set reservation in the past!")
            return
        }
        if (roomId == null) {
            emitUiMessage("Please select a room!")
            return
        }
        if (titleVal.isBlank()) {
            emitUiMessage("Title cannot be empty!")
            return
        }

        val request = AddReservationRequest(
            roomId = roomId,
            userId = userId,
            startTime = start,
            endTime = end,
            title = titleVal
        )

        updateReservationInternal(reservationId, request)
    }

    private fun updateReservationInternal(reservationId: Int, request: AddReservationRequest) {
        viewModelScope.launch {
            _editReservationState.value = EditReservationState.Loading
            val response = try {
                val token = authPreferences.token.first()
                RetrofitInstance.api.editReservation(
                    reservationId = reservationId,
                    request = request,
                    token = "Bearer $token"
                )
            } catch (e: HttpException) {
                _editReservationState.value = EditReservationState.Error("HTTP: ${e.code()}")
                return@launch
            } catch (e: Exception) {
                _editReservationState.value = EditReservationState.Error(e.message ?: "Unknown error")
                return@launch
            }

            val entity = response.toEntity()

            try {
                localRepo.cancelAlarm(entity.id)
                localRepo.updateReservation(entity)
                localRepo.scheduleForReservation(entity.id)
            } catch (e: Exception) {
                localRepo.updateReservation(entity.copy(alarmState = AlarmState.PENDING))
                _editReservationState.value = EditReservationState.AlarmFailed(e.message ?: "")
                return@launch
            }

            _editReservationState.value = EditReservationState.Success
        }
    }

    fun resetState() {
        _editReservationState.value = EditReservationState.Idle
    }
}