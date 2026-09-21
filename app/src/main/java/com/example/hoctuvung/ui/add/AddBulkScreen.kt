package com.example.hoctuvung.ui.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddBulkScreen(
    viewModel: AddWordViewModel = viewModel(factory = AddWordViewModel.factory(LocalContext.current))
) {
    var text by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("One word per line:  word | meaning | example (optional)")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Word list") },
            minLines = 8,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val original = text
            viewModel.saveBulk(original) { r ->
                val lines = original.lines()
                text = r.invalidLines.joinToString("\n") { lines[it - 1] }

                val parts = mutableListOf("Added ${r.added}")
                if (r.duplicates > 0) parts.add("skipped ${r.duplicates} duplicate")
                if (r.invalidLines.isNotEmpty()) {
                    parts.add("${r.invalidLines.size} invalid line(s): ${r.invalidLines.joinToString(", ")}")
                }
                message = parts.joinToString(", ")
            }
        }) {
            Text("Save all")
        }
        if (message.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(message)
        }
    }
}