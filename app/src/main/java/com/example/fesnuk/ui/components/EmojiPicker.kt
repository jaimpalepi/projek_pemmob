package com.example.fesnuk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun EmojiPicker(
    isVisible: Boolean,
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pick a reaction",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(commonEmojis) { emoji ->
                            EmojiItem(
                                emoji = emoji,
                                onClick = {
                                    onEmojiSelected(emoji.unicode)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiItem(
    emoji: EmojiData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji.character,
            fontSize = 24.sp
        )
    }
}

data class EmojiData(
    val character: String,
    val unicode: String,
    val name: String
)

// Common Discord-like reaction emojis
private val commonEmojis = listOf(
    EmojiData("👍", "U+1F44D", "thumbs_up"),
    EmojiData("👎", "U+1F44E", "thumbs_down"),
    EmojiData("❤️", "U+2764", "heart"),
    EmojiData("😂", "U+1F602", "joy"),
    EmojiData("😮", "U+1F62E", "open_mouth"),
    EmojiData("😢", "U+1F622", "crying_face"),
    EmojiData("😡", "U+1F621", "rage"),
    EmojiData("🔥", "U+1F525", "fire"),
    EmojiData("💯", "U+1F4AF", "hundred"),
    EmojiData("🎉", "U+1F389", "tada"),
    EmojiData("🤔", "U+1F914", "thinking"),
    EmojiData("😍", "U+1F60D", "heart_eyes"),
    EmojiData("🙄", "U+1F644", "eye_roll"),
    EmojiData("😎", "U+1F60E", "sunglasses"),
    EmojiData("🤣", "U+1F923", "rofl"),
    EmojiData("😭", "U+1F62D", "sob"),
    EmojiData("🥺", "U+1F97A", "pleading_face"),
    EmojiData("😤", "U+1F624", "triumph")
)