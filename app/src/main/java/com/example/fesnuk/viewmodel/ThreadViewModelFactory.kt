package com.example.fesnuk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fesnuk.data.ThreadRepository
import com.example.fesnuk.data.CommentRepository

class ThreadViewModelFactory(
    private val repository: ThreadRepository,
    private val commentRepository: CommentRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThreadViewModel::class.java)) {
            return ThreadViewModel(repository, commentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}