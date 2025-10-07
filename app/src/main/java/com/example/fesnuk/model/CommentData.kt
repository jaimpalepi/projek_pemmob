package com.example.fesnuk.model

import com.google.gson.annotations.SerializedName

data class CommentApiData(
    @SerializedName("id") val id: Int,
    @SerializedName("post_id") val postId: Int,
    @SerializedName("parent_id") val parentId: Int?,
    @SerializedName("content") val content: String,
    @SerializedName("attachments") val attachments: List<Any>, // Ignoring for now
    @SerializedName("reactions") val reactions: Map<String, Int>,
    @SerializedName("reply_count") val replyCount: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CommentsResponse(
    @SerializedName("data") val data: List<CommentApiData>,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int
)

data class CommentRequest(
    @SerializedName("post_id") val postId: Int,
    @SerializedName("content") val content: String
)

data class CommentReplyRequest(
    @SerializedName("post_id") val postId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("parent_id") val parentId: Int
)