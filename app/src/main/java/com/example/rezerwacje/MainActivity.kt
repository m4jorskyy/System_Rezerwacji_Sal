package com.example.rezerwacje

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.ui.navigation.AppNavigation
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RezerwacjeTheme {
                var isReady by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(2000)
                    isReady = true
                }

                if (isReady) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}