package com.example.hoctuvung.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
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
            val isListeningMode = viewModel.mode == "listening"
            
            // SPEC: Không hiện chữ trước khi trả lời đối với chế độ listening
            val showText = !isListeningMode || state.feedback != null

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Còn ${state.remainingCount} thẻ", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(24.dp))
                
                if (showText) {
                    Text(
                        text = if (state.clozeSentence != null) state.clozeSentence!! else card.meaningVi,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Nghe và gõ lại",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(Modifier.height(24.dp))

                if (isListeningMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { viewModel.playAudio(false) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Nghe")
                            Spacer(Modifier.width(4.dp))
                            Text("Nghe")
                        }
                        OutlinedButton(onClick = { viewModel.playAudio(true) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Nghe chậm")
                            Spacer(Modifier.width(4.dp))
                            Text("Nghe chậm")
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                if (state.hintText != null) {
                    Text(
                        text = "Gợi ý: ${state.hintText}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(16.dp))
                }

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
                    val isHint3 = state.hintLevel == 3
                    val isPassed = fb.correct && !isHint3
                    
                    Text(
                        if (isPassed) "Đúng!" else "Sai. Đáp án: ${fb.correctWord}",
                        color = if (isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.useHint() },
                        modifier = Modifier.weight(1f),
                        enabled = state.feedback == null && state.hintLevel < 3
                    ) {
                        Text("Gợi ý (${state.hintLevel}/3)")
                    }
                    Button(onClick = { viewModel.submitAnswer() }, modifier = Modifier.weight(1f)) {
                        Text(if (state.feedback == null) "Kiểm tra" else "Tiếp")
                    }
                }
            }
        }
    }
}
