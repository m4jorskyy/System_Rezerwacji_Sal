package com.example.rezerwacje

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.data.database.ReservationsRepository
import com.example.rezerwacje.notification.NotificationScheduler
import com.example.rezerwacje.notification.NotificationViewModel
import com.example.rezerwacje.notification.NotificationViewModelFactory
import com.example.rezerwacje.ui.navigation.AppNavigation
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = ReservationsRepository(NotificationScheduler(this))
        val factory = NotificationViewModelFactory(repo, NotificationScheduler(this))
        val vm: NotificationViewModel by viewModels { factory }


        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val reconciled = prefs.getBoolean("alarms_reconciled", false)
        val scheduled = prefs.getBoolean("alarms_scheduled", false)

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

        if (!reconciled) {
            vm.reconcileAlarms()
            prefs.edit { putBoolean("alarms_reconciled", true) }
        }
        if (!scheduled) {
            vm.scheduleAll()
            prefs.edit { putBoolean("alarms_scheduled", true) }
        }
    }
}