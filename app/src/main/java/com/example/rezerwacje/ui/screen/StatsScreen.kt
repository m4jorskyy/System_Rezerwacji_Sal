package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.model.GlobalStats
import com.example.rezerwacje.data.model.RoomStats
import com.example.rezerwacje.data.model.UserStats
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.viewmodel.StatsViewModel
import com.example.rezerwacje.ui.viewmodel.StatsViewModelFactory
import com.example.rezerwacje.ui.viewmodel.StatsState
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(navController: NavController) {
    val context = LocalContext.current

    // Inicjalizacja ViewModelu z użyciem fabryki
    val viewModel: StatsViewModel = viewModel(
        factory = StatsViewModelFactory(context)
    )

    val uiState by viewModel.statsState.collectAsState()
    val currentWeek by viewModel.currentWeekStart.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Rooms", "Users")

    // Formatowanie daty: "10 Jan - 16 Jan"
    val endDate = currentWeek.plusDays(6)
    // Używamy Locale.US dla angielskich nazw miesięcy
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.US)
    val dateRangeText = "${currentWeek.format(formatter)} - ${endDate.format(formatter)}"

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                // Nagłówek i nawigacja datą
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.prevWeek() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous week")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Stats",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = dateRangeText, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }

                    IconButton(onClick = { viewModel.nextWeek() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next week")
                    }
                }

                // Zakładki
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.RESERVATION.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(stringResource(R.string.back_text))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is StatsState.Idle -> {
                    // Pusty stan
                }
                is StatsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is StatsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.loadStats() }) {
                            Text("Retry")
                        }
                    }
                }
                // --- TU JEST ZMIANA: Przekazujemy nawigację do widoków ---
                is StatsState.StatsLoaded -> {
                    when (selectedTab) {
                        0 -> GlobalStatsView(state.globalStats)
                        1 -> RoomStatsView(
                            rooms = state.roomStats,
                            onRoomClick = { roomId ->
                                // Nawigacja do szczegółów sali
                                navController.navigate("${Screen.STATS_DETAILS.route}/room/$roomId")
                            }
                        )
                        2 -> UserStatsView(
                            users = state.userStats,
                            onUserClick = { userId ->
                                // Nawigacja do szczegółów usera
                                navController.navigate("${Screen.STATS_DETAILS.route}/user/$userId")
                            }
                        )
                    }
                }
                // Jeśli masz tu też stany RoomHistoryLoaded/UserHistoryLoaded (zależy czy rozdzieliłeś ekrany),
                // to w tym ekranie ich nie obsługujemy, bo są w StatsDetailsScreen.
                else -> {}
            }
        }
    }
}

// --- WIDOK 1: OVERVIEW (GLOBAL) ---
@Composable
fun GlobalStatsView(stats: GlobalStats?) {
    if (stats == null) return

    Column(modifier = Modifier.padding(16.dp)) {
        // Karty KPI
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "Reservations",
                value = stats.totalReservations.toString(),
                icon = Icons.Default.DateRange,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Hours",
                value = "%.1f h".format(Locale.US, stats.totalHours),
                icon = Icons.Default.Star,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Weekly Highlights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        HighlightItem(
            label = "Most Popular Room",
            value = if (stats.topRoomId != null && stats.topRoomId != 0) "Room #${stats.topRoomId}" else "No data",
            icon = Icons.Default.Home
        )
        Spacer(modifier = Modifier.height(8.dp))
        HighlightItem(
            label = "Most Active User",
            value = if (stats.topUserId != null && stats.topUserId != 0) "User #${stats.topUserId}" else "No data",
            icon = Icons.Default.Person
        )
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun HighlightItem(label: String, value: String, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodySmall)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- WIDOK 2: ROOMS ---
@Composable
fun RoomStatsView(rooms: List<RoomStats>, onRoomClick: (Int) -> Unit) {
    val sortedRooms = rooms.sortedByDescending { it.totalHours }

    if (sortedRooms.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No room data for this week.", color = Color.Gray)
        }
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(sortedRooms) { room ->
            Card(
                onClick = { onRoomClick(room.roomId) }, // Obsługa kliknięcia
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Room #${room.roomId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("%.1f h".format(Locale.US, room.totalHours), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("avg. %.1f h".format(Locale.US, room.avgHours), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${room.reservationsCount} reservations", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Pasek postępu
                    val progress = (room.totalHours / 50.0).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

// --- WIDOK 3: USERS ---
@Composable
fun UserStatsView(users: List<UserStats>, onUserClick: (Int) -> Unit) {
    val sortedUsers = users.sortedByDescending { it.totalHours }

    if (sortedUsers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No user activity.", color = Color.Gray)
        }
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        // Nagłówek tabeli
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("User", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Res.", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Total", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text("Avg.", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }
            Divider()
        }

        items(sortedUsers) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(user.userId) } // Obsługa kliknięcia
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#${user.userId}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)

                // Badge liczby rezerwacji
                Surface(
                    modifier = Modifier.weight(0.5f),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = user.reservationsCount.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                Text(
                    "%.1f h".format(Locale.US, user.totalHours),
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "%.1f h".format(Locale.US, user.avgHours),
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}