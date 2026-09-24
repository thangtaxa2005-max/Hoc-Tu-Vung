package com.example.hoctuvung.ui.review

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.local.CardEntity
import com.example.hoctuvung.data.repository.CardRepository
import com.example.hoctuvung.domain.AnswerChecker
import com.example.hoctuvung.domain.srs.Sm2Scheduler
import com.example.hoctuvung.domain.srs.SrsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ReviewUiState(
    val currentCard: CardEntity? = null,
    val remainingCount: Int = 0,
    val totalCount: Int = 0,
    val input: String = "",
    val feedback: Feedback? = null,
    val sessionDone: Boolean = false
)

data class Feedback(val correct: Boolean, val correctWord: String)

private fun CardEntity.toSrsState() = SrsState(
    repetitions = repetitions,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    lapses = lapses
)

class ReviewViewModel(
    private val repository: CardRepository,
    private val newCardsPerDay: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var queue: MutableList<CardEntity> = mutableListOf()
    private var totalAtStart: Int = 0

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            val dueCards = repository.getDueCards(today)
            val newCards = repository.getNewCards(newCardsPerDay)
            queue = (dueCards + newCards).toMutableList()
            totalAtStart = queue.size
            showNextCard()
        }
    }

    private fun showNextCard() {
        if (queue.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                currentCard = null,
                sessionDone = true,
                remainingCount = 0
            )
            return
        }
        _uiState.value = _uiState.value.copy(
            currentCard = queue.first(),
            remainingCount = queue.size,
            totalCount = totalAtStart,
            input = "",
            feedback = null,
            sessionDone = false
        )
    }

    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
    }

    fun submitAnswer() {
        val state = _uiState.value
        val card = state.currentCard ?: return

        if (state.feedback != null) {
            showNextCard()
            return
        }

        val correct = AnswerChecker.isCorrect(state.input, card.word)
        val quality = if (correct) 5 else 1

        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            val result = Sm2Scheduler.review(card.toSrsState(), quality, today)

            val updatedCard = card.copy(
                repetitions = result.state.repetitions,
                intervalDays = result.state.intervalDays,
                easeFactor = result.state.easeFactor,
                lapses = result.state.lapses,
                dueDay = result.dueDay
            )
            repository.update(updatedCard)

            queue.removeAt(0)
            if (!correct) {
                queue.add(updatedCard)
            }

            _uiState.value = _uiState.value.copy(
                feedback = Feedback(correct = correct, correctWord = card.word)
            )
        }
    }

    companion object {
        fun factory(context: Context, newCardsPerDay: Int = 10): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val db = AppDatabase.getInstance(context.applicationContext)
                    ReviewViewModel(CardRepository(db.cardDao()), newCardsPerDay)
                }
            }
    }
}
