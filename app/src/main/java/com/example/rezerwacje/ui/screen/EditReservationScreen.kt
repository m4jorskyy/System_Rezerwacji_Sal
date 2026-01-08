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
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.ui.components.RoomCard // <--- Importujemy RoomCard
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.util.showDateTimePicker
import com.example.rezerwacje.ui.viewmodel.EditReservationState
import com.example.rezerwacje.ui.viewmodel.EditReservationViewModel
import com.example.rezerwacje.ui.viewmodel.EditReservationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun EditReservationScreen(navController: NavController, reservationId: Int) {
    val context = LocalContext.current
    val viewModel: EditReservationViewModel =
        viewModel(factory = EditReservationViewModelFactory(context))

    // Pobieranie stanów z ViewModelu
    val editReservationState by viewModel.editReservationState.collectAsState()
    val availableRooms by viewModel.rooms.collectAsState()

    val title by viewModel.title.collectAsState()
    val selectedRoomId by viewModel.selectedRoomId.collectAsState()
    val startDateTime by viewModel.startDateTime.collectAsState()
    val endDateTime by viewModel.endDateTime.collectAsState()

    // Filtry
    val capacity by viewModel.capacity.collectAsState()
    val hasWhiteboard by viewModel.hasWhiteboard.collectAsState()
    val hasProjector by viewModel.hasProjector.collectAsState()
    val hasDesks by viewModel.hasDesks.collectAsState()

    val authPreferences = remember { AuthPreferences(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")

    // Obsługa wiadomości UI
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Załadowanie danych rezerwacji na starcie
    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    // Obsługa sukcesu/błędu
    LaunchedEffect(editReservationState) {
        when (val state = editReservationState) {
            is EditReservationState.Error -> {
                viewModel.emitUiMessage(state.message)
                // Nie resetujemy od razu, żeby użytkownik mógł poprawić dane
            }
            is EditReservationState.Success -> {
                viewModel.emitUiMessage(context.getString(R.string.reservation_edited))
                viewModel.resetState()
                navController.navigate(Screen.RESERVATION.route)
            }
            is EditReservationState.AlarmFailed -> {
                // Specyficzny błąd alarmu, ale rezerwacja zapisana
                viewModel.emitUiMessage("Saved, but alarm failed: ${state.message}")
                navController.navigate(Screen.RESERVATION.route)
            }
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        shape = MaterialTheme.shapes.medium
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        shape = MaterialTheme.shapes.medium
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

                // Brak przycisku "Find Rooms" - dzieje się automatycznie

                // --- WYNIKI WYSZUKIWANIA (Z ROOMCARD) ---
                if (availableRooms.isNotEmpty() || editReservationState is EditReservationState.RoomsLoaded) {
                    items(availableRooms) { room ->
                        Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                            RoomCard(
                                room = room,
                                isSelected = selectedRoomId == room.id,
                                onClick = { viewModel.onRoomSelected(room.id) }
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

                // --- TYTUŁ ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = {
                            Text(
                                stringResource(R.string.reservation_title_text),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                    )
                }

                // --- PRZYCISK ZAPISU ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val userId = authPreferences.userId.first()
                                // Przekazujemy tylko ID, reszta jest w ViewModelu
                                viewModel.onSubmit(reservationId, userId)
                            }
                        },
                        enabled = editReservationState != EditReservationState.Loading && selectedRoomId != null && title.isNotBlank(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.save_changes_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }

                item {
                    if (editReservationState is EditReservationState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

            }

            // --- PRZYCISK POWROTU ---
            Button(
                onClick = { navController.navigate(Screen.VIEW.route) },
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
fun EditReservationScreenPreview() {
    RezerwacjeTheme {
        EditReservationScreen(navController = rememberNavController(), reservationId = 1)
    }
}