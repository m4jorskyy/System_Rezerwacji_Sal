package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rezerwacje.ui.viewmodel.StatsViewModel
import com.example.rezerwacje.ui.viewmodel.StatsViewModelFactory
import com.example.rezerwacje.ui.viewmodel.StatsState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsDetailsScreen(
    navController: NavController,
    type: String, // "room" lub "user"
    id: Int
) {
    val context = LocalContext.current
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(context))
    val uiState by viewModel.statsState.collectAsState()

    // Odpalamy pobieranie danych przy wejściu na ekran
    LaunchedEffect(Unit) {
        if (type == "room") {
            viewModel.loadRoomHistory(id)
        } else {
            viewModel.loadUserHistory(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (type == "room") "Historia Sali #$id" else "Historia Usera #$id") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is StatsState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is StatsState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))

                // Wyświetlanie historii sali
                is StatsState.RoomHistoryLoaded -> {
                    HistoryList(
                        data = state.history.map { HistoryItemData(it.weekStart, it.reservationsCount, it.totalHours, it.avgHours) }
                    )
                }

                // Wyświetlanie historii usera
                is StatsState.UserHistoryLoaded -> {
                    HistoryList(
                        data = state.history.map { HistoryItemData(it.weekStart, it.reservationsCount, it.totalHours, it.avgHours) }
                    )
                }
                else -> {}
            }
        }
    }
}

// Pomocnicza klasa i komponent do wyświetlania listy (żeby nie kopiować kodu dla room i user)
data class HistoryItemData(val date: String, val count: Int, val total: Double, val avg: Double)

@Composable
fun HistoryList(data: List<HistoryItemData>) {
    // Sortujemy od najnowszej daty
    val sortedData = data.sortedByDescending { it.date }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(sortedData) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tydzień: ${item.date}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${item.count} rezerwacji", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("%.1f h".format(Locale.US, item.total), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("śr. %.1f h".format(Locale.US, item.avg), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}