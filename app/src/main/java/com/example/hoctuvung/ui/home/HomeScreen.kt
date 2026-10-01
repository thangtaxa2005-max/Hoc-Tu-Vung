package com.example.hoctuvung.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartRecall: () -> Unit,
    onStartCloze: () -> Unit,
    onStartListening: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    // Tính số từ sẽ ôn hôm nay = số từ đến hạn + số từ mới (tối đa giới hạn mỗi ngày)
    val todayTotal = state.dueCount + min(state.newCount, state.newCardsPerDay)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nhớ Từ", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cần ôn hôm nay", style = MaterialTheme.typography.titleMedium)
                    Text("$todayTotal", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("(${state.dueCount} đến hạn • ${min(state.newCount, state.newCardsPerDay)} từ mới)", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Đã thuộc (interval > 20 ngày):", style = MaterialTheme.typography.bodyLarge)
                    Text("${state.masteredCount} từ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                }
            }

            Text("Chọn chế độ ôn tập", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp))
            
            Button(onClick = onStartRecall, modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp)) {
                Icon(Icons.Default.Create, contentDescription = null); Spacer(Modifier.width(8.dp))
                Text("Gõ từ (Recall)")
            }
            Button(onClick = onStartCloze, modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.Star, contentDescription = null); Spacer(Modifier.width(8.dp))
                Text("Điền khuyết (Cloze)")
            }
            Button(onClick = onStartListening, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null); Spacer(Modifier.width(8.dp))
                Text("Luyện nghe (Listening)")
            }
        }
    }
}
