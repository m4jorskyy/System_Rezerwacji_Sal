package com.example.rezerwacje

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.ui.navigation.AppNavigation
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.notification.NotificationScheduler
import kotlinx.coroutines.delay
import com.example.rezerwacje.notification.NotificationViewModel
import com.example.rezerwacje.notification.NotificationViewModelFactory

class MainActivity : ComponentActivity() {

    private val notificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(
            ReservationsRepository(),
            NotificationScheduler(this)
        )
    }


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

        notificationViewModel.scheduleAll()
    }
}