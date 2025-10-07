package com.example.fesnuk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.model.NookData
import com.example.fesnuk.model.PostApiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NookDetailUiState(
    val nook: NookData? = null,
    val posts: List<PostApiData> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingPosts: Boolean = false,
    val errorMessage: String? = null
)

class NookDetailViewModel : ViewModel() {
    private val repository = NookRepository()

    private val _uiState = MutableStateFlow(NookDetailUiState())
    val uiState: StateFlow<NookDetailUiState> = _uiState.asStateFlow()

    fun loadNookDetail(nookId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.getNookById(nookId)
                .onSuccess { nook ->
                    _uiState.value = _uiState.value.copy(
                        nook = nook,
                        isLoading = false,
                        errorMessage = null
                    )
                    // Load posts after nook details are loaded
                    loadNookPosts(nookId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun loadNookPosts(nookId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPosts = true)
            
            repository.getPostsByNookId(nookId)
                .onSuccess { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoadingPosts = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPosts = false,
                        errorMessage = exception.message ?: "Failed to load posts"
                    )
                }
        }
    }

    fun retryLoadNookDetail(nookId: String) {
        loadNookDetail(nookId)
    }
}