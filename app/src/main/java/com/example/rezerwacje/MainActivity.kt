package com.example.rezerwacje

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.ui.navigation.AppNavigation
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

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