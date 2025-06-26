package com.example.rezerwacje.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalendarDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val CALENDAR_MAP_KEY = stringPreferencesKey("calendar_event_map")

        private val moshi = Moshi.Builder().build()
        private val mapType = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Long::class.javaObjectType
        )
        private val mapAdapter: JsonAdapter<Map<String, Long>> =
            moshi.adapter(mapType)
    }

    fun calendarMappingsFlow(): Flow<Map<Int, Long>> =
        dataStore.data
            .map { preferences ->
                val json = preferences[CALENDAR_MAP_KEY] ?: "{}"
                val strMap: Map<String, Long> = runCatching {
                    mapAdapter.fromJson(json)
                }.getOrNull() ?: emptyMap<String, Long>()

                strMap.entries.fold(mutableMapOf<Int, Long>()) { acc, (key, value) ->
                    key.toIntOrNull()?.let { acc[it] = value }
                    acc
                }
            }

    suspend fun saveMapping(reservationId: Int, eventId: Long) {
        dataStore.edit { preferences ->
            val currentJson = preferences[CALENDAR_MAP_KEY] ?: "{}"
            val strMap: MutableMap<String, Long> = runCatching {
                mapAdapter.fromJson(currentJson)
            }.getOrNull()?.toMutableMap() ?: mutableMapOf()

            strMap[reservationId.toString()] = eventId
            preferences[CALENDAR_MAP_KEY] = mapAdapter.toJson(strMap)
        }
    }

    suspend fun removeMapping(reservationId: Int) {
        dataStore.edit { preferences ->
            val json = preferences[CALENDAR_MAP_KEY] ?: "{}"
            val strMap: MutableMap<String, Long> = runCatching {
                mapAdapter.fromJson(json)
            }.getOrNull()?.toMutableMap() ?: mutableMapOf()

            strMap.remove(reservationId.toString())
            preferences[CALENDAR_MAP_KEY] = mapAdapter.toJson(strMap)
        }
    }
}
