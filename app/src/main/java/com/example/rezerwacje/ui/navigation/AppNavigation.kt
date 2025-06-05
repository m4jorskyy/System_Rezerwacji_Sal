package com.example.rezerwacje.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rezerwacje.ui.screen.AddReservation
import com.example.rezerwacje.ui.screen.AddRoomsScreen
import com.example.rezerwacje.ui.screen.EditReservationScreen
import com.example.rezerwacje.ui.screen.EditRoomScreen
import com.example.rezerwacje.ui.screen.HomeScreen
import com.example.rezerwacje.ui.screen.LoginScreen
import com.example.rezerwacje.ui.screen.RegisterScreen
import com.example.rezerwacje.ui.screen.ReservationHistoryScreen
import com.example.rezerwacje.ui.screen.ReservationScreen
import com.example.rezerwacje.ui.screen.ShowRoomsScreen
import com.example.rezerwacje.ui.screen.ViewReservationsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HOME.route) {
        composable(Screen.HOME.route) {
            HomeScreen(navController)
        }
        composable(Screen.REGISTER.route) {
            RegisterScreen(navController)
        }
        composable(Screen.LOGIN.route) {
            LoginScreen(navController)
        }
        composable(Screen.RESERVATION.route) {
            ReservationScreen(navController)
        }
        composable(Screen.HISTORY.route) {
            ReservationHistoryScreen(navController)
        }
        composable(Screen.ADD.route) {
            AddReservation(navController)
        }
        composable(Screen.VIEW.route) {
            ViewReservationsScreen(navController)
        }
        composable(Screen.ADD_ROOM.route) {
            AddRoomsScreen(navController)
        }
        composable(Screen.SHOW_ROOMS.route) {
            ShowRoomsScreen(navController)
        }
        composable("${Screen.EDIT_ROOM.route}/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull()
            roomId?.let {
                EditRoomScreen(navController, it)
            }
        }
        composable("${Screen.EDIT_RESERVATION.route}/{reservationId}") { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId")?.toIntOrNull()
            reservationId?.let {
                EditReservationScreen(navController, it)
            }
        }
    }
}