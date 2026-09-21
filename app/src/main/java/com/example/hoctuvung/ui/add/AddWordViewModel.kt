package com.example.hoctuvung.ui.add

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.repository.AddResult
import com.example.hoctuvung.data.repository.CardRepository
import kotlinx.coroutines.launch

class AddWordViewModel(private val repository: CardRepository) : ViewModel() {

    fun save(word: String, meaning: String, example: String, onResult: (AddResult) -> Unit) {
        viewModelScope.launch {
            onResult(repository.add(word, meaning, example))
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                AddWordViewModel(CardRepository(db.cardDao()))
            }
        }
    }
}