package com.example.hoctuvung.data.repository

import com.example.hoctuvung.data.local.CardDao
import com.example.hoctuvung.data.local.CardEntity
import com.example.hoctuvung.domain.BulkParser
import kotlinx.coroutines.flow.Flow

sealed interface AddResult {
    data object Added : AddResult
    data object Duplicate : AddResult
    data object Invalid : AddResult
}

data class BulkAddResult(
    val added: Int,
    val duplicates: Int,
    val invalidLines: List<Int>
)

class CardRepository(private val dao: CardDao) {

    fun observeAll(): Flow<List<CardEntity>> = dao.observeAll()

    suspend fun add(word: String, meaningVi: String, exampleSentence: String?): AddResult {
        val w = word.trim()
        val m = meaningVi.trim()
        val ex = exampleSentence?.trim()?.ifEmpty { null }

        if (w.isEmpty() || m.isEmpty()) return AddResult.Invalid

        val id = dao.insert(CardEntity(word = w, meaningVi = m, exampleSentence = ex))
        return if (id == -1L) AddResult.Duplicate else AddResult.Added
    }

    suspend fun addBulk(text: String): BulkAddResult {
        val parsed = BulkParser.parse(text)
        var added = 0
        var duplicates = 0
        for (card in parsed.cards) {
            val id = dao.insert(
                CardEntity(
                    word = card.word,
                    meaningVi = card.meaningVi,
                    exampleSentence = card.exampleSentence
                )
            )
            if (id == -1L) duplicates++ else added++
        }
        return BulkAddResult(added, duplicates, parsed.invalidLines)
    }

    suspend fun delete(card: CardEntity) = dao.delete(card)

    suspend fun getDueCards(today: Long): List<CardEntity> = dao.getDueCards(today)

    suspend fun getNewCards(limit: Int): List<CardEntity> = dao.getNewCards(limit)

    suspend fun update(card: CardEntity) = dao.update(card)
}
