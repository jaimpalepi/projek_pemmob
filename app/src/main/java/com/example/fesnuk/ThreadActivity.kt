package com.example.fesnuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fesnuk.view.theme.FesnukTheme
import com.example.fesnuk.view.screens.ThreadScreen

class ThreadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get postId from intent extras
        val postId = intent.getStringExtra("POST_ID") ?: "1"
        
        setContent {
            FesnukTheme {
                ThreadScreen(
                    postId = postId,
                    onBackClick = { finish() }
                )
            }
        }
    }
}