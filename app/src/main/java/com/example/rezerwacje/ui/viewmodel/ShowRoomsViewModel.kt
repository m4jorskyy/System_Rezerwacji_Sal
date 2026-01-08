package com.example.rezerwacje.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.data.model.RoomFilterRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ShowRoomsViewModel(private val authPreferences: AuthPreferences) : ViewModel() {
    private val _roomsState = MutableStateFlow<ShowRoomsState>(ShowRoomsState.Idle)
    val roomsState: StateFlow<ShowRoomsState> = _roomsState

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun emitUiMessage(message: String) {
        viewModelScope.launch {
            _uiMessage.emit(message)
        }
    }

    fun showRooms() {
        viewModelScope.launch {
            _roomsState.value = ShowRoomsState.Loading

            try {
                val token = authPreferences.token.first()
                val response =
                    RetrofitInstance.api.filterRooms(RoomFilterRequest(), "Bearer $token")
                _roomsState.value = ShowRoomsState.Success(response)

            } catch (e: HttpException) {
                _roomsState.value = ShowRoomsState.Error("HTTP: ${e.code()}")
            } catch (e: Exception) {
                _roomsState.value = ShowRoomsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteRoom(roomId: Int) {
        viewModelScope.launch {
            _roomsState.value = ShowRoomsState.Loading

            try {
                val token = authPreferences.token.first()
                val response = RetrofitInstance.api.deleteRoom(roomId, "Bearer $token")

                if (response.isSuccessful){
                    emitUiMessage("Pokój usunięty pomyślnie")
                    showRooms()
                } else {
                    // --- TUTAJ DODAJEMY OBSŁUGĘ BŁĘDU 409 ---
                    if (response.code() == 409) {
                        emitUiMessage("Nie można usunąć sali, która ma przypisane rezerwacje!")
                    } else {
                        emitUiMessage("Nie udało się usunąć: kod ${response.code()}")
                    }
                    showRooms() // Zdejmujemy loader
                }
            } catch (e: HttpException) {
                emitUiMessage("Błąd sieci: ${e.code()}")
                showRooms()
            } catch (e: Exception) {
                emitUiMessage("Błąd: ${e.message}")
                showRooms()
            }
        }
    }

    fun resetState() {
        _roomsState.value = ShowRoomsState.Idle
    }
}