package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.AddReservationState
import com.example.rezerwacje.ui.viewmodel.AddReservationViewModel
import com.example.rezerwacje.ui.viewmodel.AddReservationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.notification.NotificationScheduler
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

    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedRoomId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")


    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(addReservationState) {
        when (val state = addReservationState) {
            is AddReservationState.Error -> {
                viewModel.emitUiMessage(state.msg)
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showDateTimePicker(context) { selected ->
                                startDateTime = selected
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
                                endDateTime = selected
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

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (startDateTime != null && endDateTime != null) {
                                selectedRoomId = null
                                viewModel.findAvailableRooms(startDateTime!!, endDateTime!!)
                            }
                        },
                        enabled = startDateTime != null && endDateTime != null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.find_rooms_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (addReservationState is AddReservationState.RoomsLoaded) {
                    items(availableRooms) { room ->
                        Button(onClick = {
                            selectedRoomId = room.id
                        }) {
                            Text(
                                "${room.name} (${room.building})",
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    var selectedRoom = availableRooms.firstOrNull { it.id == selectedRoomId }
                    selectedRoom?.let {
                        Text(
                            text = "Chose: ${it.name} (${it.building})",
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }

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
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val userId = authPreferences.userId.first()
                                if (startDateTime != null && endDateTime != null) {
                                    viewModel.onSubmit(
                                        roomId = selectedRoomId,
                                        userId = userId,
                                        startDateTime = startDateTime,
                                        endDateTime = endDateTime,
                                        title = title
                                    )
                                }
                            }
                        },
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

                item {
                    if (addReservationState is AddReservationState.Loading) {
                        CircularProgressIndicator()
                    }
                }

            }

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