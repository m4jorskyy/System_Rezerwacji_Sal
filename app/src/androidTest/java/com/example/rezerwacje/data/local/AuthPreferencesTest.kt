package com.example.rezerwacje.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthPreferencesTest {

    private lateinit var authPreferences: AuthPreferences

    @Before
    fun setup() {
        // Pobieramy prawdziwy kontekst aplikacji testowej
        val context = ApplicationProvider.getApplicationContext<Context>()
        authPreferences = AuthPreferences(context)
    }

    @Test
    fun saveAndReadToken() = runBlocking {
        // 1. GIVEN - Mamy przykładowy token
        val testToken = "abc-123-xyz"

        // 2. WHEN - Zapisujemy go
        authPreferences.saveToken(testToken)

        // 3. THEN - Odczytujemy i sprawdzamy, czy jest taki sam
        // .first() pobiera pierwszą wartość z Flow (strumienia danych)
        val savedToken = authPreferences.token.first()

        assertEquals(testToken, savedToken)
    }

    @Test
    fun clearToken() = runBlocking {
        // 1. GIVEN - Zapisujemy token
        authPreferences.saveToken("tajny-token")

        // 2. WHEN - Czyścimy go
        authPreferences.clearToken()

        // 3. THEN - Sprawdzamy, czy wynik to null
        val savedToken = authPreferences.token.first()
        assertNull(savedToken)
    }
}