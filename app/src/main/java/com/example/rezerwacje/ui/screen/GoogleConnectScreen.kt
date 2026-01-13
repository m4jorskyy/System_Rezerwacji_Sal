package com.example.rezerwacje.ui.screen

import GoogleAuthViewModelFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rezerwacje.ui.viewmodel.GoogleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun GoogleConnectScreen(navController: NavController) { // Czy gdzie tam chcesz ten przycisk
    val context = LocalContext.current
    val viewModel: GoogleAuthViewModel = viewModel(factory = GoogleAuthViewModelFactory())

    // Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.handleSignInResult(task)
    }

    // Obsługa wyniku
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event == "SUCCESS") {
                Toast.makeText(context, "Pomyślnie połączono z Kalendarzem!", Toast.LENGTH_LONG).show()
                // Opcjonalnie: wróć gdzieś
                 navController.popBackStack()
            } else {
                Toast.makeText(context, event, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Przycisk
    Button(onClick = {
        val intent = viewModel.getSignInIntent(context)
        launcher.launch(intent)
    }) {
        Text("Połącz z Google Calendar")
    }
}