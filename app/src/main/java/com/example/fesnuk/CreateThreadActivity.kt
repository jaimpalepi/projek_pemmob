package com.example.fesnuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fesnuk.view.theme.FesnukTheme
import com.example.fesnuk.view.screens.CreateThreadScreen

class CreateThreadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FesnukTheme {
                CreateThreadScreen(
                    onCloseClick = { finish() },
                    context = this@CreateThreadActivity
                )
            }
        }
    }
}