package com.example.fesnuk.data

import com.example.fesnuk.model.CommentRequest
import com.example.fesnuk.model.CommentReplyRequest
import com.example.fesnuk.model.CommentsResponse
import com.example.fesnuk.model.ApiResponse
import retrofit2.Response

class CommentRepository(private val apiService: ApiService) {
    
    suspend fun postComment(postId: Int, content: String): Response<ApiResponse<Unit>> {
        val request = CommentRequest(postId = postId, content = content)
        return apiService.postComment(request)
    }
    
    suspend fun replyToComment(postId: Int, content: String, parentId: Int): Response<ApiResponse<Unit>> {
        val request = CommentReplyRequest(postId = postId, content = content, parentId = parentId)
        return apiService.replyToComment(request)
    }
    
    suspend fun getCommentsByPostId(postId: String): Response<CommentsResponse> {
        return apiService.getCommentsByPostId(postId)
    }
    
    suspend fun getCommentReplies(commentId: String): Response<CommentsResponse> {
        return apiService.getCommentReplies(commentId)
    }
}