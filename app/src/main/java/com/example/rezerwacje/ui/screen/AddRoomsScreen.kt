package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rezerwacje.R
import com.example.rezerwacje.ui.viewmodel.AddRoomViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rezerwacje.data.model.AddRoomRequest
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.viewmodel.AddRoomState
import com.example.rezerwacje.ui.viewmodel.AddRoomViewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.ui.theme.AppDefaults
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddRoomsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AddRoomViewModel = viewModel(
        factory = AddRoomViewModelFactory(context)
    )
    val addRoomState by viewModel.addRoomState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var roomName by remember { mutableStateOf("") }
    var buildingName by remember { mutableStateOf("") }
    var whiteboard by remember { mutableStateOf(false) }
    var projector by remember { mutableStateOf(false) }
    var desks by remember { mutableStateOf(false) }

    var roomCapacityText by remember { mutableStateOf("") }
    var floorNumberText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(addRoomState) {
        when (val state = addRoomState) {
            is AddRoomState.Error -> {
                viewModel.emitUiMessage(state.message)
                viewModel.resetState()
            }

            is AddRoomState.Success -> {
                viewModel.emitUiMessage(context.getString(R.string.room_added))
                viewModel.resetState()
                navController.navigate(Screen.RESERVATION.route)
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
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        label = {
                            Text(
                                stringResource(R.string.room_name),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        })
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = buildingName,
                        onValueChange = { buildingName = it },
                        label = {
                            Text(
                                stringResource(R.string.building_name),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        })
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = roomCapacityText,
                        onValueChange = { roomCapacityText = it },
                        label = {
                            Text(
                                stringResource(R.string.room_capacity),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        })
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = floorNumberText,
                        onValueChange = { floorNumberText = it },
                        label = {
                            Text(
                                stringResource(R.string.floor_number),
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        })
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            stringResource(R.string.whiteboard)
                        )
                        Checkbox(
                            checked = whiteboard,
                            onCheckedChange = { whiteboard = it }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = stringResource(R.string.projector)
                        )
                        Checkbox(
                            checked = projector,
                            onCheckedChange = { projector = it },
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            stringResource(R.string.desks)
                        )
                        Checkbox(
                            checked = desks,
                            onCheckedChange = { desks = it },
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.onSubmit(
                                name = roomName,
                                building = buildingName,
                                capacity = roomCapacityText,
                                floor = floorNumberText,
                                whiteboard = whiteboard,
                                projector = projector,
                                desks = desks
                            )
                        },
                        enabled =
                            addRoomState !is AddRoomState.Loading &&
                                    roomName.isNotEmpty() &&
                                    buildingName.isNotEmpty() &&
                                    roomCapacityText.isNotEmpty() &&
                                    floorNumberText.isNotEmpty(),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        colors = AppDefaults.buttonColors(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.add_room),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    if (addRoomState is AddRoomState.Loading) {
                        CircularProgressIndicator()
                    }
                }
            }

            Button(
                onClick = {
                    navController.navigate(Screen.RESERVATION.route)
                    viewModel.resetState()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = AppDefaults.buttonColors(),
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
fun AddRoomsScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        AddRoomsScreen(navController = navController)
    }
}