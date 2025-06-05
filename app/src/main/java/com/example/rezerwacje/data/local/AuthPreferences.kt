package com.example.rezerwacje.data.local

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class AuthPreferences(private val context: Context) {
    private val tokenKey = stringPreferencesKey("token")
    private val userIdKey = intPreferencesKey("userId")
    private val userRoleKey = stringPreferencesKey("userRole")
    private val userNameKey = stringPreferencesKey("userName")

    val userId: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[userIdKey] ?: -1 }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[tokenKey] }

    val userRole: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[userRoleKey] }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[userNameKey] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { it[userIdKey] = userId }
    }

    suspend fun saveUserRole(userRole: String){
        context.dataStore.edit { it[userRoleKey] = userRole }
    }

    suspend fun saveUserName(userName: String){
        context.dataStore.edit { it[userNameKey] = userName }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { it.remove(userIdKey) }
    }
}