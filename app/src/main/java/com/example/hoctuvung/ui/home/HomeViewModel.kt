package com.example.hoctuvung.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class HomeUiState(
    val dueCount: Int = 0,
    val newCount: Int = 0,
    val masteredCount: Int = 0,
    val newCardsPerDay: Int = 10
)

class HomeViewModel(
    repository: CardRepository,
    private val newCardsPerDay: Int
) : ViewModel() {
    
    private val today = LocalDate.now().toEpochDay()
    
    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeDueCount(today),
        repository.observeNewCount(),
        repository.observeMasteredCount()
    ) { due, newCards, mastered ->
        HomeUiState(
            dueCount = due,
            newCount = newCards,
            masteredCount = mastered,
            newCardsPerDay = newCardsPerDay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(newCardsPerDay = newCardsPerDay)
    )

    companion object {
        fun factory(context: Context, newCardsPerDay: Int = 10): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val db = AppDatabase.getInstance(context.applicationContext)
                    HomeViewModel(CardRepository(db.cardDao(), db.reviewLogDao()), newCardsPerDay)
                }
            }
    }
}
