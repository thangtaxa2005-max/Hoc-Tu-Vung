package com.example.hoctuvung.ui.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hoctuvung.data.repository.AddResult

@Composable
fun AddWordScreen(
    onBulkClick: () -> Unit,
    viewModel: AddWordViewModel = viewModel(factory = AddWordViewModel.factory(LocalContext.current))
) {
    var word by rememberSaveable { mutableStateOf("") }
    var meaning by rememberSaveable { mutableStateOf("") }
    var example by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("English word") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = meaning,
            onValueChange = { meaning = it },
            label = { Text("Vietnamese meaning") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = example,
            onValueChange = { example = it },
            label = { Text("Example sentence (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.save(word, meaning, example) { result ->
                message = when (result) {
                    AddResult.Added -> {
                        word = ""
                        meaning = ""
                        example = ""
                        "Added"
                    }
                    AddResult.Duplicate -> "This word already exists"
                    AddResult.Invalid -> "Word and meaning are required"
                }
            }
        }) {
            Text("Save")
        }
        if (message.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(message)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBulkClick) {
            Text("Add many words at once")
        }
    }
}