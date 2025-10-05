package com.example.fesnuk.data

import com.example.fesnuk.Post
import com.example.fesnuk.R

class NookRepository {

    fun getPosts(): List<Post> {
        return listOf(
            Post(
                nook = "::AnimeMemes",
                timeAgo = "51m ago",
                title = "BAKUSHINNNN!!!!!!!",
                caption = "Are you bakumaxxing rn?",
                postImage = R.drawable.placeholder_image,
                replyCount = 45
            ),
            Post(
                nook = "::AnimeMemes",
                timeAgo = "51m ago",
                title = "How much money have Kazuya spent on renting?",
                caption = "I assume one morbillion yen or so...",
                postImage = null,
                replyCount = 45
            )
        )
    }
}
