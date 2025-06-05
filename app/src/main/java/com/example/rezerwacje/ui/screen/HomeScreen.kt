package com.example.rezerwacje.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rezerwacje.R
import com.example.rezerwacje.ui.navigation.Screen
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.ui.theme.AppDefaults
import com.example.rezerwacje.ui.theme.RezerwacjeTheme

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .padding(innerPadding)
        ) {

            Button(
                onClick = {
                    try {
                        navController.navigate(Screen.REGISTER.route)
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Register route failed", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = AppDefaults.buttonColors(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.register_text),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    try {
                        navController.navigate(Screen.LOGIN.route)
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Login route failed", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = AppDefaults.buttonColors(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.login_text),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}