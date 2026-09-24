package com.example.hoctuvung.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ôn tập") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.sessionDone) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Xong phiên ôn tập!", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onFinished) { Text("Về trang chủ") }
                }
                return@Box
            }

            val card = state.currentCard ?: return@Box

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Còn ${state.remainingCount} thẻ",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    card.meaningVi,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.input,
                    onValueChange = viewModel::onInputChange,
                    label = { Text("Gõ từ tiếng Anh") },
                    singleLine = true,
                    enabled = state.feedback == null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                state.feedback?.let { fb ->
                    Text(
                        if (fb.correct) "Đúng!" else "Sai. Đáp án: ${fb.correctWord}",
                        color = if (fb.correct)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = { viewModel.submitAnswer() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.feedback == null) "Kiểm tra" else "Tiếp")
                }
            }
        }
    }
}
