package com.example.hoctuvung.data.repository

import com.example.hoctuvung.data.local.CardDao
import com.example.hoctuvung.data.local.CardEntity
import kotlinx.coroutines.flow.Flow

sealed interface AddResult {
    data object Added : AddResult
    data object Duplicate : AddResult
    data object Invalid : AddResult
}

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

    suspend fun delete(card: CardEntity) = dao.delete(card)
}