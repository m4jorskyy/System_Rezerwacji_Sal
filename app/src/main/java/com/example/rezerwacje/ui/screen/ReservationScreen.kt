package com.example.rezerwacje.ui.screen

import GoogleAuthViewModelFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.rezerwacje.ui.viewmodel.GoogleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch

@Composable
fun ReservationScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authPreferences = AuthPreferences(context)
    val scope = rememberCoroutineScope()
    val role by authPreferences.userRole.collectAsState(initial = "")
    val name by authPreferences.userName.collectAsState(initial = "")

    // 1. Inicjalizacja ViewModelu do Google Auth
    val googleAuthViewModel: GoogleAuthViewModel = viewModel(factory = GoogleAuthViewModelFactory())

    // 2. Launcher - to on otwiera okno Google i odbiera wynik
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        googleAuthViewModel.handleSignInResult(task)
    }

    // 3. Nasłuchiwanie na wynik operacji (Sukces / Błąd)
    LaunchedEffect(Unit) {
        googleAuthViewModel.uiEvent.collect { event ->
            if (event == "SUCCESS") {
                Toast.makeText(context, "Połączono z Kalendarzem Google!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, event, Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            // ... (Reszta przycisków bez zmian) ...
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.ADD.route) },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                ) {
                    Text(stringResource(R.string.add_reservation), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.VIEW.route) },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                ) {
                    Text(stringResource(R.string.view_reservations), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(
                    onClick = { navController.navigate(Screen.HISTORY.route) },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                ) {
                    Text(stringResource(R.string.reservation_history), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 4. ZMODYFIKOWANY PRZYCISK GOOGLE
            item {
                Button(
                    onClick = {
                        // Zamiast nawigacji, odpalamy intent Google
                        val intent = googleAuthViewModel.getSignInIntent(context)
                        googleSignInLauncher.launch(intent)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        stringResource(R.string.google), // Np. "Połącz z Google"
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }
            }

            if (role == "ADMIN") {
                // ... (Reszta przycisków Admina bez zmian) ...
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Button(
                        onClick = { navController.navigate(Screen.ADD_ROOM.route) },
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                    ) {
                        Text(stringResource(R.string.add_room), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Button(
                        onClick = { navController.navigate(Screen.SHOW_ROOMS.route) },
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                    ) {
                        Text(stringResource(R.string.show_rooms), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp))}
                item {
                    Button(
                        onClick = { navController.navigate(Screen.SHOW_STATS.route) },
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
                    ) {
                        Text(stringResource(R.string.show_stats), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
                    }
                }
            }
        }

        // ... (Logout Dialog bez zmian) ...
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text(stringResource(R.string.logout), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
        }

        if (showLogoutDialog) {
            // ... (AlertDialog bez zmian) ...
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(stringResource(R.string.logout_title_text)) },
                text = { Text(stringResource(R.string.logout_text)) },
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
                    }) { Text(stringResource(R.string.yes_text)) }
                },
                dismissButton = {
                    Button(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.no_text)) }
                }
            )
        }
    }
}