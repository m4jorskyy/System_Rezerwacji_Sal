package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.ui.components.ReservationCard // <--- Nowy import
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.ViewReservationsState
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModel
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModelFactory
import java.time.LocalDateTime

@Composable
fun ReservationHistoryScreen(navController: NavController) {
    val viewModel: ViewReservationsViewModel = viewModel(
        factory = ViewReservationsViewModelFactory(LocalContext.current)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                }

                when (val state = reservationsState) {
                    is ViewReservationsState.Success -> {
                        val now = LocalDateTime.now()
                        // Filtrujemy tylko przeszłe
                        val past = state.reservations.filter { it.endTime.isBefore(now) }

                        if (past.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_reservation_history),
                                    modifier = Modifier.padding(16.dp),
                                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                )
                            }
                        } else {
                            items(items = past, key = { it.id }) { reservation ->
                                // Używamy Boxa do marginesów bocznych (16dp lub 32dp w zależności od gustu)
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ReservationCard(
                                        reservation = reservation
                                        // Nie przekazujemy onEdit/onDelete, bo to historia
                                        // Karta wyświetli się bez menu opcji
                                    )
                                }
                            }
                        }
                    }

                    is ViewReservationsState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary
                            )
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
fun ReservationHistoryScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        ReservationHistoryScreen(navController = navController)
    }
}