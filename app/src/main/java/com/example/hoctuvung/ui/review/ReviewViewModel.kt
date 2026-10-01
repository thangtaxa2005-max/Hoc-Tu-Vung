package com.example.hoctuvung.ui.review

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.local.CardEntity
import com.example.hoctuvung.data.local.ReviewLogEntity
import com.example.hoctuvung.data.repository.CardRepository
import com.example.hoctuvung.domain.AnswerChecker
import com.example.hoctuvung.domain.srs.Sm2Scheduler
import com.example.hoctuvung.domain.srs.SrsState
import com.example.hoctuvung.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.ceil

data class ReviewUiState(
    val currentCard: CardEntity? = null,
    val remainingCount: Int = 0,
    val totalCount: Int = 0,
    val input: String = "",
    val feedback: Feedback? = null,
    val sessionDone: Boolean = false,
    val hintLevel: Int = 0,
    val hintText: String? = null,
    val clozeSentence: String? = null
)

data class Feedback(val correct: Boolean, val correctWord: String)

private fun CardEntity.toSrsState() = SrsState(
    repetitions = repetitions,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    lapses = lapses
)

class ReviewViewModel(
    val mode: String,
    private val repository: CardRepository,
    private val newCardsPerDay: Int,
    private val ttsManager: TtsManager
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
        val nextCard = queue.first()
        _uiState.value = _uiState.value.copy(
            currentCard = nextCard,
            remainingCount = queue.size,
            totalCount = totalAtStart,
            input = "",
            feedback = null,
            sessionDone = false,
            hintLevel = 0,
            hintText = null,
            clozeSentence = if (mode == "cloze") com.example.hoctuvung.domain.ClozeBuilder.build(nextCard.word, nextCard.exampleSentence) else null
        )
        
        // Tự động phát âm ngay khi hiện thẻ nếu là chế độ listening
        if (mode == "listening") playAudio()
    }

    fun playAudio(slow: Boolean = false) {
        val card = _uiState.value.currentCard ?: return
        val textToSpeak = card.exampleSentence ?: card.word
        ttsManager.speak(textToSpeak, slow)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown() // Giải phóng tài nguyên loa khi tắt màn hình
    }

    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
    }

    fun useHint() {
        val state = _uiState.value
        val card = state.currentCard ?: return
        
        if (state.hintLevel >= 3 || state.feedback != null) return

        val newLevel = state.hintLevel + 1
        val word = card.word
        
        val revealCount = when (newLevel) {
            1 -> 1
            2 -> ceil(word.length / 2.0).toInt()
            else -> word.length
        }
        
        val hintString = word.mapIndexed { index, c ->
            if (c == ' ') ' ' else if (index < revealCount) c else '_'
        }.joinToString("")

        _uiState.value = state.copy(
            hintLevel = newLevel,
            hintText = hintString
        )
    }

    fun submitAnswer() {
        val state = _uiState.value
        val card = state.currentCard ?: return

        if (state.feedback != null) {
            showNextCard()
            return
        }

        val correct = AnswerChecker.isCorrect(state.input, card.word)
        val isHint3 = state.hintLevel == 3
        
        val quality = if (!correct || isHint3) {
            1
        } else {
            when (state.hintLevel) {
                0 -> 5
                1 -> 4
                2 -> 3
                else -> 1
            }
        }

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
            repository.insertLog(
                ReviewLogEntity(
                    cardId = card.id, mode = mode, reviewedAt = System.currentTimeMillis(),
                    quality = quality, hintsUsed = state.hintLevel, correct = correct
                )
            )

            queue.removeAt(0)
            if (!correct || isHint3) queue.add(updatedCard)

            _uiState.value = _uiState.value.copy(
                feedback = Feedback(correct = correct, correctWord = card.word)
            )
        }
    }

    companion object {
        fun factory(context: Context, mode: String, newCardsPerDay: Int = 10): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val db = AppDatabase.getInstance(context.applicationContext)
                    ReviewViewModel(
                        mode, CardRepository(db.cardDao(), db.reviewLogDao()), newCardsPerDay,
                        TtsManager(context.applicationContext)
                    )
                }
            }
    }
}
