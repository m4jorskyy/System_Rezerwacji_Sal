package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.rezerwacje.data.model.AddRoomRequest
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.EditRoomState
import com.example.rezerwacje.ui.viewmodel.EditRoomViewModel
import com.example.rezerwacje.ui.viewmodel.EditRoomViewModelFactory

@Composable
fun EditRoomScreen(navController: NavController, roomId: Int) {
    val context = LocalContext.current
    val viewModel: EditRoomViewModel = viewModel(
        factory = EditRoomViewModelFactory(context)
    )

    val editRoomState by viewModel.editRoomState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadRoom(roomId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(editRoomState) {
        when (val state = editRoomState) {
            is EditRoomState.Error -> {
                viewModel.emitUiMessage(state.message)
                viewModel.resetState()
            }

            is EditRoomState.Success -> {
                viewModel.emitUiMessage("Room edited successfully")
                viewModel.resetState()
                navController.navigate(Screen.SHOW_ROOMS.route)
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
            when (val state = editRoomState) {
                is EditRoomState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                is EditRoomState.RoomLoaded -> {
                    var name by remember { mutableStateOf(state.room.name) }
                    var building by remember { mutableStateOf(state.room.building) }
                    var capacity by remember { mutableIntStateOf(state.room.capacity) }
                    var floor by remember { mutableIntStateOf(state.room.floor) }
                    var whiteboard by remember { mutableStateOf(state.room.whiteboard) }
                    var projector by remember { mutableStateOf(state.room.projector) }
                    var desks by remember { mutableStateOf(state.room.desks) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = {
                                    Text(
                                        "Room Name",
                                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = building,
                                onValueChange = { building = it },
                                label = {
                                    Text(
                                        "Building Name",
                                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = capacity.toString(),
                                onValueChange = { capacity = it.toIntOrNull() ?: 0 },
                                label = {
                                    Text(
                                        "Capacity",
                                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = floor.toString(),
                                onValueChange = { floor = it.toIntOrNull() ?: 0 },
                                label = {
                                    Text(
                                        "Floor",
                                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Whiteboard",
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                            Checkbox(
                                checked = whiteboard,
                                onCheckedChange = { whiteboard = it }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Projector",
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                            Checkbox(
                                checked = projector,
                                onCheckedChange = { projector = it }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Desks",
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                            Checkbox(
                                checked = desks,
                                onCheckedChange = { desks = it }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = {
                                viewModel.onSubmit(
                                    roomId,
                                    name,
                                    building,
                                    capacity,
                                    floor,
                                    whiteboard,
                                    projector,
                                    desks
                                )
                            }) {
                                Text(
                                    "Edit Room",
                                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                else -> {}
            }

            Button(
                onClick = { navController.navigate(Screen.SHOW_ROOMS.route) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Back",
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditRoomScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        EditRoomScreen(navController = navController, roomId = 0)
    }
}
