package com.example.fesnuk

data class Post(
    val nook: String,
    val timeAgo: String,
    val title: String,
    val caption: String,
    val replyCount: Int,
    // Gunakan Int untuk drawable resource, atau String jika URL dari internet
    val postImage: Int? = null
)
    