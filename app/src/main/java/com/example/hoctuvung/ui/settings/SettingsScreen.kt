package com.example.hoctuvung.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.hoctuvung.ui.about.AboutActivity
import com.example.hoctuvung.ui.video.VideoActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cài đặt", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            
            Text("Số thẻ mới mỗi ngày: ${state.newCardsPerDay}", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = state.newCardsPerDay.toFloat(),
                onValueChange = { viewModel.updateNewCardsPerDay(it.toInt()) },
                valueRange = 5f..50f,
                steps = 8
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            Text(
                "Tốc độ phát âm (TTS): ${String.format(java.util.Locale.US, "%.1f", state.ttsSpeed)}x",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = state.ttsSpeed,
                onValueChange = { viewModel.updateTtsSpeed(it) },
                valueRange = 0.5f..2.0f,
                steps = 14
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bật nhắc nhở ôn tập", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = state.reminderEnabled,
                    onCheckedChange = viewModel::updateReminderEnabled
                )
            }
            
            if (state.reminderEnabled) {
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val hourStr = state.reminderHour.toString().padStart(2, '0')
                    val minStr = state.reminderMinute.toString().padStart(2, '0')
                    Text(text = "Thời gian: $hourStr:$minStr", style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(Modifier.width(16.dp))
                    
                    Button(
                        onClick = { 
                            val newHour = if (state.reminderHour == 23) 0 else state.reminderHour + 1
                            viewModel.updateReminderTime(newHour, state.reminderMinute)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Giờ", maxLines = 1) }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            val newMin = if (state.reminderMinute >= 50) 0 else state.reminderMinute + 10
                            viewModel.updateReminderTime(state.reminderHour, newMin)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Phút", maxLines = 1) }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Khác", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val context = LocalContext.current

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dictionary.cambridge.org"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tra cứu từ điển Cambridge") }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { context.startActivity(Intent(context, VideoActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Học từ vựng qua bài hát") }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { context.startActivity(Intent(context, AboutActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Thông tin phiên bản") }
        }
    }
}
