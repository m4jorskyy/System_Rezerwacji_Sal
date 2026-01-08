package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class StatsViewModel(private val authPreferences: AuthPreferences) : ViewModel() {

    private val _statsState = MutableStateFlow<StatsState>(StatsState.Idle)
    val statsState: StateFlow<StatsState> = _statsState.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    private val _currentWeekStart = MutableStateFlow(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    val currentWeekStart = _currentWeekStart.asStateFlow()

    init {
        loadStats()
    }

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun nextWeek() {
        _currentWeekStart.value = _currentWeekStart.value.plusWeeks(1)
        loadStats()
    }

    fun prevWeek() {
        _currentWeekStart.value = _currentWeekStart.value.minusWeeks(1)
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _statsState.value = StatsState.Loading

            try {
                val token = authPreferences.token.first()
                val authHeader = "Bearer $token"
                val dateStr = _currentWeekStart.value.format(DateTimeFormatter.ISO_LOCAL_DATE)

                val global = RetrofitInstance.api.getGlobalStats(dateStr, authHeader)
                val rooms = RetrofitInstance.api.getAllRoomsStatsByWeek(dateStr, authHeader)
                val users = RetrofitInstance.api.getAllUsersStatsByWeek(dateStr, authHeader)

                _statsState.value = StatsState.StatsLoaded(
                    globalStats = global,
                    roomStats = rooms,
                    userStats = users
                )

            } catch (e: HttpException) {
                val msg = "Błąd serwera: ${e.code()}"
                _statsState.value = StatsState.Error(msg)
                emitUiMessage(msg)
            } catch (e: Exception) {
                val msg = e.message ?: "Nieznany błąd"
                _statsState.value = StatsState.Error(msg)
                emitUiMessage(msg)
            }
        }
    }

    fun loadRoomHistory(roomId: Int) {
        viewModelScope.launch {
            _statsState.value = StatsState.Loading
            try {
                val token = authPreferences.token.first()
                val authHeader = "Bearer $token"

                // Wołamy endpoint historii
                val history = com.example.rezerwacje.data.api.RetrofitInstance.api.getRoomStatsById(roomId, authHeader)

                _statsState.value = StatsState.RoomHistoryLoaded(history)
            } catch (e: Exception) {
                _statsState.value = StatsState.Error(e.message ?: "Błąd pobierania historii sali")
            }
        }
    }

    // Metoda do pobierania historii Użytkownika
    fun loadUserHistory(userId: Int) {
        viewModelScope.launch {
            _statsState.value = StatsState.Loading
            try {
                val token = authPreferences.token.first()
                val authHeader = "Bearer $token"

                val history = com.example.rezerwacje.data.api.RetrofitInstance.api.getUsersStatsById(userId, authHeader)

                _statsState.value = StatsState.UserHistoryLoaded(history)
            } catch (e: Exception) {
                _statsState.value = StatsState.Error(e.message ?: "Błąd pobierania historii użytkownika")
            }
        }
    }

    // Metoda powrotu do głównego widoku (przeładowuje dashboard)
    fun backToDashboard() {
        loadStats() // To Twoja istniejąca funkcja ładująca główny ekran
    }

    fun resetState() {
        _statsState.value = StatsState.Idle
    }
}