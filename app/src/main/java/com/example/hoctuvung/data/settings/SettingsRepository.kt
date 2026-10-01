package com.example.hoctuvung.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    
    companion object {
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val NEW_CARDS_PER_DAY = intPreferencesKey("new_cards_per_day")
        val TTS_SPEED = floatPreferencesKey("tts_speed")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data
        .map { preferences ->
            UserSettings(
                reminderHour = preferences[REMINDER_HOUR] ?: 20,
                reminderMinute = preferences[REMINDER_MINUTE] ?: 0,
                reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
                newCardsPerDay = preferences[NEW_CARDS_PER_DAY] ?: 10,
                ttsSpeed = preferences[TTS_SPEED] ?: 1.0f
            )
        }

    suspend fun updateReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_HOUR] = hour
            preferences[REMINDER_MINUTE] = minute
        }
    }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED] = enabled
        }
    }

    suspend fun updateNewCardsPerDay(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[NEW_CARDS_PER_DAY] = count
        }
    }

    suspend fun updateTtsSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[TTS_SPEED] = speed
        }
    }
}

data class UserSettings(
    val reminderHour: Int,
    val reminderMinute: Int,
    val reminderEnabled: Boolean,
    val newCardsPerDay: Int,
    val ttsSpeed: Float
)
