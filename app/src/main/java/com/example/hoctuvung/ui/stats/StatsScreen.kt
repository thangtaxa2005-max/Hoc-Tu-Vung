package com.example.hoctuvung.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Thống kê") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Mức độ nhớ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Từ mới", color = MaterialTheme.colorScheme.primary); Text("${state.newCount}") }
                        Column { Text("Đang học", color = MaterialTheme.colorScheme.secondary); Text("${state.learningCount}") }
                        Column { Text("Đã thuộc", color = MaterialTheme.colorScheme.tertiary); Text("${state.masteredCount}") }
                    }
                }
            }

            item {
                Text("Số lượt ôn 7 ngày qua", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (state.dailyCounts.isEmpty()) Text("Chưa có dữ liệu")
                        state.dailyCounts.forEach {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(it.dateStr)
                                Text("${it.count} lượt", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text("Từ hay sai nhất (lapses)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            if (state.mostFailedCards.isEmpty()) {
                item { Text("Chưa có từ nào bị sai nhiều lần.") }
            } else {
                items(state.mostFailedCards) { card ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(card.word, fontWeight = FontWeight.Bold)
                                Text(card.meaningVi, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("Sai: ${card.lapses} lần", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
