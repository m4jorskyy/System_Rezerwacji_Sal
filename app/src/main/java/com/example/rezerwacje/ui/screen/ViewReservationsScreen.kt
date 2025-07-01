package com.example.rezerwacje.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.model.Reservation
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.viewmodel.ViewReservationsState
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModel
import com.example.rezerwacje.ui.viewmodel.ViewReservationsViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                            val ongoing =
                                reservation.startTime.isBefore(now) && reservation.endTime.isAfter(
                                    now
                                )
                            val upcoming = reservation.startTime.isAfter(now)
                            ongoing || upcoming
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
                                val dismissState = rememberDismissState(
                                    confirmStateChange = { value ->
                                        when (value) {
                                            DismissValue.DismissedToStart -> {
                                                viewModel.deleteReservation(reservation.id)
                                                false
                                            }

                                            DismissValue.DismissedToEnd -> {
                                                navController.navigate("${Screen.EDIT_RESERVATION.route}/${reservation.id}")
                                                false
                                            }

                                            else -> true
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
                                                Color(0xFF4CAF50) // mocna zieleń
                                            )

                                            DismissDirection.EndToStart -> Triple(
                                                Alignment.CenterEnd,
                                                Icons.Default.Delete,
                                                Color(0xFFF44336) // mocna czerwień
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
                                                .background(Color.Transparent),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (direction == DismissDirection.StartToEnd) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(bgColor)
                                                        .padding(start = 12.dp),
                                                    contentAlignment = Alignment.CenterStart,
                                                ) {
                                                    icon?.let {
                                                        Icon(
                                                            imageVector = it,
                                                            contentDescription = null,
                                                            tint = Color.Black
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            if (direction == DismissDirection.EndToStart) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(bgColor)
                                                        .padding(end = 12.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    icon?.let {
                                                        Icon(
                                                            imageVector = it,
                                                            contentDescription = null,
                                                            tint = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    dismissContent = {
                                        ReservationItem(reservation)
                                    },
                                    modifier = Modifier.padding(16.dp)
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

@Composable
fun ReservationItem(reservation: Reservation, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Surface(
        modifier = modifier
            .fillMaxWidth()             // najpierw padding/spacing
            .padding(8.dp),             // albo przenieś padding tu
        tonalElevation = 2.dp,
        color = backgroundColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.title,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .graphicsLayer {
                            rotationZ = rotationAngle
                        }
                )
            }


            if (expanded) {
                Spacer(modifier = modifier.height(8.dp))
                val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")

                Text(
                    text = "Start time: ${reservation.startTime.format(formatter)}"
                )
                Text(
                    text = "End time: ${reservation.endTime.format(formatter)}"
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