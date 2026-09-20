package com.example.hoctuvung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hoctuvung.ui.navigation.AppNavigation
import com.example.hoctuvung.ui.theme.HocTuVungTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HocTuVungTheme {
                AppNavigation()
            }
        }
    }
}