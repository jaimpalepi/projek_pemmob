package com.example.fesnuk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fesnuk.view.theme.FesnukTheme
import com.example.fesnuk.view.screens.NookDetailScreen

class NooksActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FesnukTheme {
                NookDetailScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}
