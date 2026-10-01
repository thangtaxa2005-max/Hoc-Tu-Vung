package com.example.hoctuvung.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.settings.SettingsRepository
import com.example.hoctuvung.data.settings.UserSettings
import com.example.hoctuvung.worker.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val context: Context,
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<UserSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(20, 0, false, 10, 1.0f)
        )

    fun updateReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { 
            repository.updateReminderEnabled(enabled) 
            val state = uiState.value
            ReminderScheduler.schedule(context, state.reminderHour, state.reminderMinute, enabled)
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch { 
            repository.updateReminderTime(hour, minute) 
            if (uiState.value.reminderEnabled) {
                ReminderScheduler.schedule(context, hour, minute, true)
            }
        }
    }

    fun updateNewCardsPerDay(count: Int) {
        viewModelScope.launch { repository.updateNewCardsPerDay(count) }
    }

    fun updateTtsSpeed(speed: Float) {
        viewModelScope.launch { repository.updateTtsSpeed(speed) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appCtx = context.applicationContext
                SettingsViewModel(appCtx, SettingsRepository(appCtx))
            }
        }
    }
}
