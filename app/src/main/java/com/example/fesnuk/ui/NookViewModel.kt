package com.example.fesnuk.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fesnuk.Post
import com.example.fesnuk.data.NookRepository

class NookViewModel : ViewModel() {

    private val repository = NookRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    fun loadPosts() {
        _posts.value = repository.getPosts()
    }
}
