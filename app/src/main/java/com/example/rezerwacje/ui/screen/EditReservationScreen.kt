package com.example.rezerwacje.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.EditReservationState
import com.example.rezerwacje.ui.viewmodel.EditReservationViewModel
import com.example.rezerwacje.ui.viewmodel.EditReservationViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun showDateTimePicker(
    context: android.content.Context, onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val now = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context, { _, y, m, d ->
            TimePickerDialog(
                context, { _, h, min ->
                    val date = LocalDate.of(y, m + 1, d)
                    val time = LocalTime.of(h, min)
                    onDateTimeSelected(date.atTime(time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
            ).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = now.timeInMillis

    datePickerDialog.show()
}

@Composable
fun EditReservationScreen(navController: NavController, reservationId: Int) {
    val context = LocalContext.current
    val viewModel: EditReservationViewModel =
        viewModel(factory = EditReservationViewModelFactory(context))
    val editReservationState by viewModel.editReservationState.collectAsState()
    val availableRooms by viewModel.rooms.collectAsState()
    val reservation by viewModel.reservation.collectAsState()

    val authPreferences = remember { AuthPreferences(context) }
    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedRoomId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var savingTriggered by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    LaunchedEffect(reservation) {
        reservation?.let {
            startDateTime = it.startTime
            endDateTime = it.endTime
            selectedRoomId = it.roomId
            title = it.title
        }
    }

    LaunchedEffect(editReservationState) {
        when (val state = editReservationState) {
            is EditReservationState.Error -> {
                viewModel.emitUiMessage(state.message)
                viewModel.resetState()
            }

            is EditReservationState.Success -> {
                viewModel.emitUiMessage(context.getString(R.string.reservation_edited))
                viewModel.resetState()
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showDateTimePicker(context) { selected ->
                                startDateTime = selected
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
                                endDateTime = selected
                            }
                        },
                        enabled = startDateTime != null,
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

                if (editReservationState is EditReservationState.RoomsLoaded) {
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

                    val selectedRoom = availableRooms.firstOrNull { it.id == selectedRoomId }
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
                                        reservationId = reservationId,
                                        roomId = selectedRoomId,
                                        userId = userId,
                                        date = startDateTime!!.toLocalDate(),
                                        startTimeStr = startDateTime!!.toLocalTime().toString(),
                                        endTimeStr = endDateTime!!.toLocalTime().toString(),
                                        title = title
                                    )
                                    savingTriggered = true
                                }
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
                    if (editReservationState is EditReservationState.Loading && savingTriggered) {
                        CircularProgressIndicator()
                    }
                }

            }
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