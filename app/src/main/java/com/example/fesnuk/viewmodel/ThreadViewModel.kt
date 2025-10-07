package com.example.fesnuk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fesnuk.data.ThreadRepository
import com.example.fesnuk.data.CommentRepository
import com.example.fesnuk.model.PostApiData
import com.example.fesnuk.model.CommentApiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ThreadUiState(
    val post: PostApiData? = null,
    val comments: List<CommentApiData> = emptyList(),
    val commentReplies: Map<Int, List<CommentApiData>> = emptyMap(),
    val isLoading: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isPostingComment: Boolean = false,
    val errorMessage: String? = null,
    val commentErrorMessage: String? = null,
    val replyingToCommentId: Int? = null,
    val expandedComments: Set<Int> = emptySet()
)

class ThreadViewModel(
    private val repository: ThreadRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ThreadUiState())
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()
    
    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            repository.getPostById(postId)
                .onSuccess { post ->
                    _uiState.value = _uiState.value.copy(
                        post = post,
                        isLoading = false
                    )
                    // Load comments after post is loaded
                    loadComments(postId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }
    
    fun loadComments(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingComments = true,
                commentErrorMessage = null
            )
            
            try {
                val response = commentRepository.getCommentsByPostId(postId)
                if (response.isSuccessful) {
                    val comments = response.body()?.data ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        comments = comments,
                        isLoadingComments = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingComments = false,
                        commentErrorMessage = "Failed to load comments"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingComments = false,
                    commentErrorMessage = e.message
                )
            }
        }
    }
    
    fun loadCommentReplies(commentId: Int) {
        viewModelScope.launch {
            try {
                val response = commentRepository.getCommentReplies(commentId.toString())
                if (response.isSuccessful) {
                    val replies = response.body()?.data ?: emptyList()
                    val currentReplies = _uiState.value.commentReplies.toMutableMap()
                    currentReplies[commentId] = replies
                    _uiState.value = _uiState.value.copy(
                        commentReplies = currentReplies
                    )
                }
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
    
    fun postComment(postId: Int, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPostingComment = true,
                commentErrorMessage = null
            )
            
            try {
                val response = commentRepository.postComment(postId, content)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isPostingComment = false)
                    // Reload comments after posting
                    loadComments(postId.toString())
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPostingComment = false,
                        commentErrorMessage = "Failed to post comment"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPostingComment = false,
                    commentErrorMessage = e.message
                )
            }
        }
    }
    
    fun replyToComment(postId: Int, content: String, parentId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPostingComment = true,
                commentErrorMessage = null
            )
            
            try {
                val response = commentRepository.replyToComment(postId, content, parentId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isPostingComment = false,
                        replyingToCommentId = null // Clear reply state
                    )
                    // Reload comments and replies after posting
                    loadComments(postId.toString())
                    loadCommentReplies(parentId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPostingComment = false,
                        commentErrorMessage = "Failed to post reply"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPostingComment = false,
                    commentErrorMessage = e.message
                )
            }
        }
    }
    
    fun startReplyToComment(commentId: Int) {
        _uiState.value = _uiState.value.copy(replyingToCommentId = commentId)
        // Also expand the comment to show replies
        toggleCommentExpansion(commentId)
    }
    
    fun cancelReply() {
        _uiState.value = _uiState.value.copy(replyingToCommentId = null)
    }
    
    fun toggleCommentExpansion(commentId: Int) {
        val currentExpanded = _uiState.value.expandedComments.toMutableSet()
        if (currentExpanded.contains(commentId)) {
            currentExpanded.remove(commentId)
        } else {
            currentExpanded.add(commentId)
            // Load replies when expanding
            loadCommentReplies(commentId)
        }
        _uiState.value = _uiState.value.copy(expandedComments = currentExpanded)
    }
    
    fun refreshPost(postId: String) {
        loadPost(postId)
    }
}