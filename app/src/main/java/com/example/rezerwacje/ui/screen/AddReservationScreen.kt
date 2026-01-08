package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.notification.NotificationScheduler
import com.example.rezerwacje.ui.components.RoomCard // <--- WAŻNY IMPORT
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.util.showDateTimePicker
import com.example.rezerwacje.ui.viewmodel.AddReservationState
import com.example.rezerwacje.ui.viewmodel.AddReservationViewModel
import com.example.rezerwacje.ui.viewmodel.AddReservationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun AddReservation(navController: NavController) {
    val context = LocalContext.current
    val scheduler = NotificationScheduler(context)
    val repo = ReservationsRepository(scheduler)
    val authPreferences = remember { AuthPreferences(context) }
    val viewModel: AddReservationViewModel = viewModel(
        factory = AddReservationViewModelFactory(context, repo)
    )
    val snackbarHostState = remember { SnackbarHostState() }

    val addReservationState by viewModel.addReservationState.collectAsState()
    val availableRooms by viewModel.rooms.collectAsState()

    val startDateTime by viewModel.startDateTime.collectAsState()
    val endDateTime by viewModel.endDateTime.collectAsState()

    val capacity by viewModel.capacity.collectAsState()
    val hasWhiteboard by viewModel.hasWhiteboard.collectAsState()
    val hasProjector by viewModel.hasProjector.collectAsState()
    val hasDesks by viewModel.hasDesks.collectAsState()

    // --- LOKALNE STANY UI ---
    var selectedRoomId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")


    // Obsługa komunikatów (Toast/Snackbar)
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(addReservationState) {
        when (val state = addReservationState) {
            is AddReservationState.Error -> {
                viewModel.emitUiMessage(state.message) // Pamiętaj o .message zamiast .msg jeśli zmieniłeś w VM
                viewModel.resetState()
            }
            is AddReservationState.ReservationSuccess -> {
                viewModel.emitUiMessage(context.getString(R.string.reservation_added))
                viewModel.resetState()
                navController.navigate(Screen.VIEW.route)
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- SEKCJA DAT ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showDateTimePicker(context) { selected ->
                                viewModel.onStartTimeChange(selected)
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = startDateTime?.format(formatter)?.toString()
                                ?: stringResource(R.string.select_start),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showDateTimePicker(context) { selected ->
                                if (startDateTime != null && selected.isBefore(startDateTime)) return@showDateTimePicker
                                viewModel.onEndTimeChange(selected)
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = endDateTime?.format(formatter)?.toString()
                                ?: stringResource(R.string.select_end),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }

                // --- FILTRY ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = { viewModel.onCapacityChange(it) },
                        label = { Text("Minimum Capacity") },
                        placeholder = { Text("e.g. 5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("Required Equipment:", style = MaterialTheme.typography.bodyMedium)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = hasWhiteboard,
                                onCheckedChange = { viewModel.onWhiteboardChange(it) }
                            )
                            Text("Whiteboard")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = hasProjector,
                                onCheckedChange = { viewModel.onProjectorChange(it) }
                            )
                            Text("Projector")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = hasDesks,
                                onCheckedChange = { viewModel.onDesksChange(it) }
                            )
                            Text("Desks")
                        }
                    }
                }

                // --- LOADING ---
                item {
                    if (addReservationState is AddReservationState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

                // --- LISTA DOSTĘPNYCH SAL (Z UŻYCIEM ROOMCARD) ---
                if (availableRooms.isNotEmpty() || addReservationState is AddReservationState.RoomsLoaded) {
                    items(availableRooms) { room ->
                        // Używamy Boxa, aby nadać marginesy boczne (32.dp) identyczne jak reszta elementów
                        Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                            RoomCard(
                                room = room,
                                isSelected = selectedRoomId == room.id,
                                onClick = { selectedRoomId = room.id }
                                // onEdit i onDelete pomijamy, bo tutaj tylko wybieramy
                            )
                        }
                    }
                }

                // --- PODSUMOWANIE WYBORU ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    val selectedRoom = availableRooms.firstOrNull { it.id == selectedRoomId }
                    selectedRoom?.let {
                        Text(
                            text = "Chosen: ${it.name} (${it.building})",
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }

                // --- TYTUŁ REZERWACJI ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                stringResource(R.string.reservation_title_text),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                }

                // --- PRZYCISK DODANIA ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val userId = authPreferences.userId.first()
                                // Używamy nowej metody onSubmit (bez przekazywania dat, VM je zna)
                                viewModel.onSubmit(
                                    roomId = selectedRoomId,
                                    userId = userId,
                                    title = title
                                )
                            }
                        },
                        // Blokujemy przycisk, jeśli ładuje lub brak wybranej sali/tytułu
                        enabled = addReservationState != AddReservationState.Loading && selectedRoomId != null && title.isNotBlank(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.add_reservation),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }
            }

            // --- PRZYCISK POWROTU ---
            Button(
                onClick = { navController.navigate(Screen.RESERVATION.route) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(R.string.back_text),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddReservationPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        AddReservation(navController = navController)
    }
}