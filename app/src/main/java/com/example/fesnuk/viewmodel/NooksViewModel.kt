package com.example.fesnuk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.model.NookData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NooksUiState(
    val nooks: List<NookData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NooksViewModel : ViewModel() {
    private val repository = NookRepository()

    private val _uiState = MutableStateFlow(NooksUiState())
    val uiState: StateFlow<NooksUiState> = _uiState.asStateFlow()

    init {
        loadNooks()
    }

    fun loadNooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.getNooks()
                .onSuccess { nooks ->
                    _uiState.value = _uiState.value.copy(
                        nooks = nooks,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun retryLoadNooks() {
        loadNooks()
    }
}