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
import androidx.compose.material.CircularProgressIndicator
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
import com.example.rezerwacje.ui.components.RoomCard
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.ShowRoomsState
import com.example.rezerwacje.ui.viewmodel.ShowRoomsViewModel
import com.example.rezerwacje.ui.viewmodel.ShowRoomsViewModelFactory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowRoomsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ShowRoomsViewModel = viewModel(factory = ShowRoomsViewModelFactory(context))
    val uiState by viewModel.roomsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.showRooms()
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ShowRoomsState.Error) {
            viewModel.emitUiMessage("Error loading rooms")
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
                item { Spacer(modifier = Modifier.height(16.dp)) }

                when (val state = uiState) {
                    is ShowRoomsState.Loading -> item {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    is ShowRoomsState.Success -> {
                        val rooms = state.rooms
                        if (rooms.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_rooms_available),
                                    modifier = Modifier.padding(16.dp),
                                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                )
                            }
                        } else {
                            items(rooms, key = { it.id }) { room ->

                                // --- SWIPE LOGIC ---
                                val dismissState = rememberDismissState(
                                    confirmStateChange = { value ->
                                        when (value) {
                                            DismissValue.DismissedToStart -> {
                                                viewModel.deleteRoom(room.id)
                                                false
                                            }
                                            DismissValue.DismissedToEnd -> {
                                                navController.navigate("${Screen.EDIT_ROOM.route}/${room.id}")
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
                                                Color(0xFF4CAF50) // Green
                                            )
                                            DismissDirection.EndToStart -> Triple(
                                                Alignment.CenterEnd,
                                                Icons.Default.Delete,
                                                Color(0xFFF44336) // Red
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
                                                .padding(vertical = 4.dp)
                                                .background(bgColor, shape = MaterialTheme.shapes.medium),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (direction == DismissDirection.StartToEnd) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(start = 24.dp),
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

                                            if (direction == DismissDirection.EndToStart) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(end = 24.dp),
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
                                        // --- TUTAJ JEST ZMIANA ---
                                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            RoomCard(
                                                room = room,
                                                // 1. Akcja Edycji (Menu)
                                                onEdit = {
                                                    navController.navigate("${Screen.EDIT_ROOM.route}/${room.id}")
                                                },
                                                // 2. Akcja Usuwania (Menu) - DODANO TO, żeby kropki się pojawiły
                                                onDelete = {
                                                    viewModel.deleteRoom(room.id)
                                                },
                                                // Opcjonalne kliknięcie w samą kartę
                                                onClick = {
                                                    // Np. pokaż szczegóły
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(vertical = 4.dp)
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
fun ShowRoomsScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        ShowRoomsScreen(navController = navController)
    }
}