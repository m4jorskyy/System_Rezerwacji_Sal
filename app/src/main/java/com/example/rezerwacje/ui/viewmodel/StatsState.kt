package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.model.GlobalStats
import com.example.rezerwacje.data.model.RoomStats
import com.example.rezerwacje.data.model.UserStats

sealed class StatsState {
    object Idle : StatsState()
    object Loading : StatsState()

    // W stanie sukcesu przekazujemy pobrane dane
    data class StatsLoaded(
        val globalStats: GlobalStats,
        val roomStats: List<RoomStats>,
        val userStats: List<UserStats>
    ) : StatsState()
    data class RoomHistoryLoaded(val history: List<RoomStats>) : StatsState()
    data class UserHistoryLoaded(val history: List<UserStats>) : StatsState()

    data class Error(val message: String): StatsState()
}