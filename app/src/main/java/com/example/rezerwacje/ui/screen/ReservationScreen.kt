package com.example.rezerwacje.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.local.AuthPreferences
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import kotlinx.coroutines.launch

@Composable
fun ReservationScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authPreferences = AuthPreferences(context)
    val scope = rememberCoroutineScope()
    val role by authPreferences.userRole.collectAsState(initial = "")
    val name by authPreferences.userName.collectAsState(initial = "")

    BackHandler(enabled = true) {}

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = "Hello, $name!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.ADD.route) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        stringResource(R.string.add_reservation),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.VIEW.route) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        stringResource(R.string.view_reservations),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.HISTORY.route) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        stringResource(R.string.reservation_history),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }
            }
            if (role == "ADMIN") {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Button(
                        onClick = { navController.navigate(Screen.ADD_ROOM.route) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            stringResource(R.string.add_room),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Button(
                        onClick = { navController.navigate(Screen.SHOW_ROOMS.route) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            stringResource(R.string.show_rooms),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp))}
                item {
                    Button(
                        onClick = { navController.navigate(Screen.SHOW_STATS.route) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            stringResource(R.string.show_stats),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }
            }
        }

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.logout),
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        stringResource(R.string.logout_title_text),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.logout_text),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showLogoutDialog = false
                        scope.launch {
                            authPreferences.clearToken()
                            authPreferences.clearUserId()
                        }
                        navController.navigate(Screen.LOGIN.route) {
                            popUpTo(Screen.LOGIN.route) { inclusive = true }
                        }
                    }) {
                        Text(
                            stringResource(R.string.yes_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { showLogoutDialog = false }) {
                        Text(
                            stringResource(R.string.no_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationScreenPreview() {
    RezerwacjeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        ReservationScreen(navController = rememberNavController())
    }
}
