package com.example.hoctuvung.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(factory = LibraryViewModel.factory(LocalContext.current))
) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()

    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No words yet")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(cards, key = { it.id }) { card ->
                ListItem(
                    headlineContent = { Text(card.word) },
                    supportingContent = { Text(card.meaningVi) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.delete(card) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}