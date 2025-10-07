package com.example.fesnuk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fesnuk.model.PostApiData
import com.example.fesnuk.data.NookRepository
import kotlinx.coroutines.launch

class NookViewModel : ViewModel() {

    private val repository = NookRepository()

    private val _posts = MutableLiveData<List<PostApiData>>()
    val posts: LiveData<List<PostApiData>> = _posts

    fun loadPosts() {
        viewModelScope.launch {
            repository.getPosts().onSuccess { postList ->
                _posts.value = postList
            }.onFailure {
                _posts.value = emptyList()
            }
        }
    }
}
