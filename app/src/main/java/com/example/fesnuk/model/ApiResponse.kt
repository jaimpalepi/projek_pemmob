package com.example.fesnuk.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: T
)

data class NookApiData(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Attachment(
    @SerializedName("type")
    val type: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("original_file_name")
    val originalFileName: String
)

data class CreatePostRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("nook_id")
    val nookId: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>
)

data class PostApiData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("nook_id")
    val nookId: String,
    @SerializedName("nook_name")
    val nookName: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("reactions")
    val reactions: Map<String, Int>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class ReactionRequest(
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("unicode")
    val unicode: String,
    @SerializedName("action")
    val action: Int // 1 for up, -1 for down
)

typealias NooksResponse = ApiResponse<List<NookApiData>>
typealias NookResponse = ApiResponse<NookApiData>
typealias CreatePostResponse = ApiResponse<PostApiData>
typealias PostsResponse = ApiResponse<List<PostApiData>>