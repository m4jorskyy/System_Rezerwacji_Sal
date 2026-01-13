package com.example.rezerwacje.ui.viewmodel

import com.example.rezerwacje.data.local.AuthPreferences
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `initial state is Idle`() {
        // 1. GIVEN - Mockujemy zależności
        // Tworzymy fałszywe AuthPreferences, bo ViewModel ich wymaga w konstruktorze.
        // 'relaxed = true' oznacza: "jeśli ViewModel wywoła coś z tego mocka, nie panikuj, po prostu nic nie rób"
        val mockPreferences = mockk<AuthPreferences>(relaxed = true)

        // 2. WHEN - Tworzymy ViewModel
        val viewModel = LoginViewModel(mockPreferences)

        // 3. THEN - Sprawdzamy stan początkowy
        // Oczekujemy, że na starcie stan logowania to Idle (Bezczynny)
        assertEquals(LoginState.Idle, viewModel.loginState.value)
    }

    @Test
    fun `resetState sets state back to Idle`() {
        // 1. GIVEN
        val mockPreferences = mockk<AuthPreferences>(relaxed = true)
        val viewModel = LoginViewModel(mockPreferences)

        // 2. WHEN - Wywołujemy reset
        viewModel.resetState()

        // 3. THEN
        assertEquals(LoginState.Idle, viewModel.loginState.value)
    }
}