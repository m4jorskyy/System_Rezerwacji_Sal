package com.example.rezerwacje.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rezerwacje.BuildConfig
import com.example.rezerwacje.data.api.RetrofitInstance
import com.example.rezerwacje.data.model.GoogleAuthRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope // <--- Import
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GoogleAuthViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    // ID Klienta WEBOWEGO (z console.google.com -> Credentials -> Web Client)
    private val WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID

    fun getSignInIntent(context: Context): Intent {
        // Definiujemy uprawnienia, o które prosimy (musi pasować do tego co masz w React)
        val calendarScope = Scope("https://www.googleapis.com/auth/calendar.events")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(WEB_CLIENT_ID) // Prosimy o kod dla backendu
            .requestEmail()
            .requestScopes(calendarScope) // <--- PROSIMY O DOSTĘP DO KALENDARZA
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        // Wylogowujemy na starcie, żeby user mógł wybrać konto (np. jeśli ma kilka)
        googleSignInClient.signOut()

        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val authCode = account.serverAuthCode

            if (authCode != null) {
                // Mamy kod -> wysyłamy na serwer
                sendCodeToBackend(authCode)
            } else {
                viewModelScope.launch { _uiEvent.emit("Błąd: Google nie dało kodu") }
            }
        } catch (e: ApiException) {
            Log.e("GoogleAuth", "SignIn failed code=" + e.statusCode)
            viewModelScope.launch { _uiEvent.emit("Anulowano lub błąd Google (kod: ${e.statusCode})") }
        }
    }

    private fun sendCodeToBackend(code: String) {
        viewModelScope.launch {
            try {
                // Strzał do API
                val response = RetrofitInstance.api.sendGoogleCode(GoogleAuthRequest(code))

                if (response.isSuccessful) {
                    // Serwer przyjął kod -> Sukces
                    _uiEvent.emit("SUCCESS")
                } else {
                    _uiEvent.emit("Błąd serwera: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiEvent.emit("Błąd połączenia: ${e.message}")
            }
        }
    }
}