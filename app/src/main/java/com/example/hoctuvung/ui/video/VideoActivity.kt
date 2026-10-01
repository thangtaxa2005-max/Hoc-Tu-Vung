package com.example.hoctuvung.ui.video

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.example.hoctuvung.R
import com.example.hoctuvung.ui.theme.HocTuVungTheme

class VideoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HocTuVungTheme {
                VideoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Học từ vựng qua bài hát", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                val videoView = VideoView(context)
                val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.vocab_song}")
                videoView.setVideoURI(uri)
                val controller = MediaController(context)
                controller.setAnchorView(videoView)
                videoView.setMediaController(controller)
                videoView.start()
                videoView
            }
        )
    }
}
