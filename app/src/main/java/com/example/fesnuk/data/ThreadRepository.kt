package com.example.fesnuk.data

import com.example.fesnuk.model.PostApiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThreadRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getPostById(id: String): Result<PostApiData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPostById(id)
                if (response.isSuccessful) {
                    val postResponse = response.body()
                    if (postResponse != null) {
                        Result.success(postResponse.data)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch post with id $id, code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}