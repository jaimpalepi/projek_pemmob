package com.example.fesnuk.data

import com.example.fesnuk.model.NooksResponse
import com.example.fesnuk.model.NookResponse
import com.example.fesnuk.model.CreatePostRequest
import com.example.fesnuk.model.CreatePostResponse
import com.example.fesnuk.model.PostsResponse
import com.example.fesnuk.model.ReactionRequest
import com.example.fesnuk.model.ApiResponse
import com.example.fesnuk.model.CommentRequest
import com.example.fesnuk.model.CommentReplyRequest
import com.example.fesnuk.model.CommentsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("nooks")
    suspend fun getNooks(): Response<NooksResponse>
    
    @GET("nooks/{id}")
    suspend fun getNookById(@Path("id") id: String): Response<NookResponse>
    
    @GET("posts")
    suspend fun getPosts(): Response<PostsResponse>
    
    @GET("posts/nook/{nook_id}")
    suspend fun getPostsByNookId(@Path("nook_id") nookId: String): Response<PostsResponse>
    
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<CreatePostResponse>
    
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<CreatePostResponse>
    
    @POST("posts/react")
    suspend fun addReaction(@Body request: ReactionRequest): Response<ApiResponse<Unit>>
    
    // Comment endpoints
    @POST("comments")
    suspend fun postComment(@Body request: CommentRequest): Response<ApiResponse<Unit>>
    
    @POST("comments/reply")
    suspend fun replyToComment(@Body request: CommentReplyRequest): Response<ApiResponse<Unit>>
    
    @GET("comments/post/{post_id}")
    suspend fun getCommentsByPostId(@Path("post_id") postId: String): Response<CommentsResponse>
    
    @GET("comments/{comment_id}/replies")
    suspend fun getCommentReplies(@Path("comment_id") commentId: String): Response<CommentsResponse>
    
    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Header("x-ms-blob-type") blobType: String = "BlockBlob",
        @Body file: RequestBody
    ): Response<Unit>
}