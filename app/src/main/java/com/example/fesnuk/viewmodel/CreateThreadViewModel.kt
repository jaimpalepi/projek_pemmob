package com.example.fesnuk.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.model.Attachment
import com.example.fesnuk.model.NookData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.InputStream

data class CreateThreadUiState(
    val nooks: List<NookData> = emptyList(),
    val selectedNook: NookData? = null,
    val title: String = "",
    val content: String = "",
    val selectedImages: List<Uri> = emptyList(),
    val isLoadingNooks: Boolean = false,
    val isUploadingFiles: Boolean = false,
    val isCreatingPost: Boolean = false,
    val uploadProgress: Float = 0f,
    val errorMessage: String? = null,
    val isPostCreated: Boolean = false,
    val createdPostId: String? = null,
    val preselectedNookId: String? = null
)

class CreateThreadViewModel(private val context: Context) : ViewModel() {
    private val repository = NookRepository()

    private val _uiState = MutableStateFlow(CreateThreadUiState())
    val uiState: StateFlow<CreateThreadUiState> = _uiState.asStateFlow()

    init {
        loadNooks()
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.readBytes()
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                it.getString(nameIndex)
            } else {
                "image.jpg" // fallback
            }
        } ?: "image.jpg"
    }

    fun loadNooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingNooks = true, errorMessage = null)
            
            repository.getNooks()
                .onSuccess { nooks ->
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        nooks = nooks,
                        isLoadingNooks = false,
                        errorMessage = null
                    )
                    
                    // Auto-select preselected nook if available
                    currentState.preselectedNookId?.let { nookId ->
                        val preselectedNook = nooks.find { it.id == nookId }
                        preselectedNook?.let { selectNook(it) }
                    }
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingNooks = false,
                        errorMessage = exception.message ?: "Failed to load nooks"
                    )
                }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun selectNook(nook: NookData) {
        _uiState.value = _uiState.value.copy(selectedNook = nook)
    }

    fun selectNookById(nookId: String) {
        _uiState.value = _uiState.value.copy(preselectedNookId = nookId)
        val nook = _uiState.value.nooks.find { it.id == nookId }
        if (nook != null) {
            selectNook(nook)
        }
        // If nook is not found yet, it will be selected when nooks are loaded
    }

    fun addImages(images: List<Uri>) {
        val currentImages = _uiState.value.selectedImages
        _uiState.value = _uiState.value.copy(selectedImages = currentImages + images)
    }

    fun removeImage(uri: Uri) {
        val currentImages = _uiState.value.selectedImages
        _uiState.value = _uiState.value.copy(selectedImages = currentImages - uri)
    }

    fun createPost() {
        val currentState = _uiState.value
        
        if (currentState.selectedNook == null) {
            _uiState.value = currentState.copy(errorMessage = "Please select a nook")
            return
        }
        
        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter a title")
            return
        }
        
        if (currentState.content.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter content")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(
                    isUploadingFiles = true,
                    uploadProgress = 0f,
                    errorMessage = null
                )

                // Upload attachments if any
                val uploadJobs = currentState.selectedImages.mapIndexed { index, uri ->
                    async {
                        _uiState.value = _uiState.value.copy(
                            uploadProgress = (index.toFloat() / currentState.selectedImages.size) * 0.8f // 80% for uploads
                        )
                        
                        val fileBytes = uriToByteArray(uri)
                        val originalFileName = getFileNameFromUri(uri)
                        
                        if (fileBytes != null) {
                            repository.uploadFile(fileBytes, originalFileName)
                                .onSuccess { fileName ->
                                    val fileExtension = originalFileName.substringAfterLast(".", "jpg")
                                    return@async Attachment(
                                        type = "image",
                                        format = fileExtension,
                                        content = fileName,
                                        originalFileName = originalFileName
                                    )
                                }
                                .onFailure { exception ->
                                    _uiState.value = _uiState.value.copy(
                                        isUploadingFiles = false,
                                        errorMessage = "Failed to upload image: ${exception.message}"
                                    )
                                    return@async null
                                }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isUploadingFiles = false,
                                errorMessage = "Failed to read image file"
                            )
                            return@async null
                        }
                        return@async null
                    }
                }
                
                // Wait for all uploads to complete
                val uploadResults = uploadJobs.awaitAll()
                val attachments = uploadResults.filterNotNull()
                
                // Check if any uploads failed
                if (uploadResults.size != attachments.size) {
                    // Some uploads failed, error message already set above
                    return@launch
                }

                // Update progress to show file uploads complete
                _uiState.value = _uiState.value.copy(
                    isUploadingFiles = false,
                    isCreatingPost = true,
                    uploadProgress = 0.8f
                )

                // Create the post
                repository.createPost(
                    title = currentState.title,
                    content = currentState.content,
                    nookId = currentState.selectedNook?.id ?: "",
                    attachments = attachments
                ).onSuccess { postId ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingPost = false,
                        uploadProgress = 1f,
                        isPostCreated = true,
                        createdPostId = postId
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingPost = false,
                        errorMessage = "Failed to create post: ${exception.message}"
                    )
                }

            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingFiles = false,
                    isCreatingPost = false,
                    errorMessage = "Unexpected error: ${exception.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetPostCreation() {
        _uiState.value = _uiState.value.copy(
            title = "",
            content = "",
            selectedImages = emptyList(),
            selectedNook = null,
            isPostCreated = false,
            createdPostId = null,
            uploadProgress = 0f,
            errorMessage = null
        )
    }
}