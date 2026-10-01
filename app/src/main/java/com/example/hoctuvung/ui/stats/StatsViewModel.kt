package com.example.hoctuvung.ui.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.local.CardEntity
import com.example.hoctuvung.data.local.DailyReviewCount
import com.example.hoctuvung.data.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val newCount: Int = 0,
    val learningCount: Int = 0,
    val masteredCount: Int = 0,
    val mostFailedCards: List<CardEntity> = emptyList(),
    val dailyCounts: List<DailyReviewCount> = emptyList()
)

class StatsViewModel(repository: CardRepository) : ViewModel() {
    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeNewCount(),
        repository.observeLearningCount(),
        repository.observeMasteredCount(),
        repository.getMostFailedCards(),
        repository.getDailyReviewCounts()
    ) { newC, learnC, masterC, failed, daily ->
        StatsUiState(newC, learnC, masterC, failed, daily)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                StatsViewModel(CardRepository(db.cardDao(), db.reviewLogDao()))
            }
        }
    }
}
