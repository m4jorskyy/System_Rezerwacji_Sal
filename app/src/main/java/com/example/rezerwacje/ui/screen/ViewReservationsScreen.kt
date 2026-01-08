package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.ui.components.ReservationCard
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.viewmodel.ViewReservationsState
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModel
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModelFactory
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewReservationsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ViewReservationsViewModel = viewModel(
        factory = ViewReservationsViewModelFactory(context)
    )
    val reservationsState by viewModel.reservationsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getUserReservations()
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(reservationsState) {
        if (reservationsState is ViewReservationsState.Error) {
            viewModel.emitUiMessage("Error loading reservations")
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
                item { Spacer(modifier = Modifier.height(16.dp)) }

                when (val state = reservationsState) {
                    is ViewReservationsState.Loading -> item {
                        CircularProgressIndicator()
                    }

                    is ViewReservationsState.Success -> {
                        val now = LocalDateTime.now()
                        val upcoming = state.reservations.filter { reservation ->
                            val ongoing = reservation.startTime.isBefore(now) && reservation.endTime.isAfter(now)
                            val isFuture = reservation.startTime.isAfter(now)
                            ongoing || isFuture
                        }

                        if (upcoming.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_upcoming_reservations),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(upcoming, key = { it.id }) { reservation ->

                                // --- LOGIKA SWIPE ---
                                val dismissState = rememberDismissState(
                                    confirmStateChange = { value ->
                                        when (value) {
                                            DismissValue.DismissedToStart -> {
                                                // Przesunięcie w lewo -> Usuń
                                                viewModel.deleteReservation(reservation.id)
                                                false // false, bo lista odświeży się po usunięciu z DB
                                            }
                                            DismissValue.DismissedToEnd -> {
                                                // Przesunięcie w prawo -> Edytuj
                                                navController.navigate("${Screen.EDIT_RESERVATION.route}/${reservation.id}")
                                                false
                                            }
                                            else -> false
                                        }
                                    }
                                )

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(
                                        DismissDirection.StartToEnd,
                                        DismissDirection.EndToStart
                                    ),
                                    background = {
                                        val direction = dismissState.dismissDirection
                                        val (align, icon, bgColor) = when (direction) {
                                            DismissDirection.StartToEnd -> Triple(
                                                Alignment.CenterStart,
                                                Icons.Default.Edit,
                                                Color(0xFF4CAF50) // Zielony (Edycja)
                                            )
                                            DismissDirection.EndToStart -> Triple(
                                                Alignment.CenterEnd,
                                                Icons.Default.Delete,
                                                Color(0xFFF44336) // Czerwony (Usuwanie)
                                            )
                                            else -> Triple(
                                                Alignment.CenterStart,
                                                null,
                                                Color.Transparent
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(vertical = 8.dp) // Dopasowanie do marginesu karty
                                                .background(bgColor, shape = MaterialTheme.shapes.medium), // Zaokrąglenie tła swipe
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (direction == DismissDirection.StartToEnd) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(start = 24.dp),
                                                    contentAlignment = Alignment.CenterStart,
                                                ) {
                                                    icon?.let { Icon(it, null, tint = Color.White) }
                                                }
                                            }

                                            if (direction == DismissDirection.EndToStart) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(end = 24.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    icon?.let { Icon(it, null, tint = Color.White) }
                                                }
                                            }
                                        }
                                    },
                                    dismissContent = {
                                        // --- TUTAJ DODAJEMY OBSŁUGĘ MENU ---
                                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            ReservationCard(
                                                reservation = reservation,
                                                // Dodanie tych funkcji włącza przycisk menu "trzy kropki"
                                                onEdit = {
                                                    navController.navigate("${Screen.EDIT_RESERVATION.route}/${reservation.id}")
                                                },
                                                onDelete = {
                                                    viewModel.deleteReservation(reservation.id)
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            Button(
                onClick = { navController.navigate(Screen.RESERVATION.route) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
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
fun ViewReservationsScreenPreview() {
    val navController = rememberNavController()
    ViewReservationsScreen(navController = navController)
}