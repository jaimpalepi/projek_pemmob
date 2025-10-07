package com.example.fesnuk.data

import android.net.Uri
import com.example.fesnuk.Post
import com.example.fesnuk.R
import com.example.fesnuk.model.NookData
import com.example.fesnuk.model.NookApiData
import com.example.fesnuk.model.CreatePostRequest
import com.example.fesnuk.model.Attachment
import com.example.fesnuk.model.PostApiData
import com.example.fesnuk.model.ReactionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import android.util.Log

class NookRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getNooks(): Result<List<NookData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNooks()
                if (response.isSuccessful) {
                    val nooksResponse = response.body()
                    if (nooksResponse != null) {
                        val nookDataList = nooksResponse.data.map { apiNook ->
                            NookData(
                                id = apiNook.id,
                                name = apiNook.name,
                                description = apiNook.description,
                                backgroundImageUrl = null // API doesn't provide image URL
                            )
                        }
                        Result.success(nookDataList)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getNookById(id: String): Result<NookData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNookById(id)
                if (response.isSuccessful) {
                    val nookResponse = response.body()
                    if (nookResponse != null) {
                        val nookData = NookData(
                            id = nookResponse.data.id,
                            name = nookResponse.data.name,
                            description = nookResponse.data.description,
                            backgroundImageUrl = null // API doesn't provide image URL
                        )
                        Result.success(nookData)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun uploadFile(fileBytes: ByteArray, originalFileName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val fileExtension = originalFileName.substringAfterLast(".", "")
                val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
                val uploadUrl = "https://fesnukberust.blob.core.windows.net/storage/attachments/$uniqueFileName?sp=rcl&st=2025-10-05T06:39:13Z&se=2045-10-05T14:54:13Z&spr=https&sv=2024-11-04&sr=c&sig=ujGh09%2BexMGTeOwF9XEoFgOOBQzizcr3ouJHKyAnOJ8%3D"
                
                val requestBody = fileBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val response = apiService.uploadFile(uploadUrl, "BlockBlob", requestBody)
                
                if (response.isSuccessful) {
                    Result.success(uniqueFileName)
                } else {
                    Result.failure(Exception("File upload failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPosts(): Result<List<PostApiData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPosts()
                if (response.isSuccessful) {
                    val postsResponse = response.body()
                    if (postsResponse != null) {
                        Result.success(postsResponse.data)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch posts with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPostsByNookId(nookId: String): Result<List<PostApiData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPostsByNookId(nookId)
                if (response.isSuccessful) {
                    val postsResponse = response.body()
                    if (postsResponse != null) {
                        Result.success(postsResponse.data)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch posts for nook $nookId with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createPost(
        title: String,
        content: String,
        nookId: String,
        attachments: List<Attachment>
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreatePostRequest(
                    title = title,
                    content = content,
                    nookId = nookId,
                    attachments = attachments
                )
                
                val response = apiService.createPost(request)
                if (response.isSuccessful) {
                    val postResponse = response.body()
                    if (postResponse != null) {
                        Result.success(postResponse.data.id.toString())
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Post creation failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addReaction(postId: Int, unicode: String, action: Int): Result<Unit> {
        Log.d("NookRepository", "addReaction called - postId: $postId, unicode: $unicode, action: $action")
        return withContext(Dispatchers.IO) {
            try {
                val request = ReactionRequest(
                    postId = postId,
                    unicode = unicode,
                    action = action
                )
                
                Log.d("NookRepository", "Making API request to posts/react with request: $request")
                val response = apiService.addReaction(request)
                
                if (response.isSuccessful) {
                    Log.d("NookRepository", "addReaction API call successful - postId: $postId, unicode: $unicode, action: $action")
                    Result.success(Unit)
                } else {
                    val errorMsg = "Reaction failed with code: ${response.code()}"
                    Log.e("NookRepository", errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e("NookRepository", "addReaction failed with exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Remove the old getPosts method that returns List<Post>
    // Keep only the suspend fun getPosts(): Result<List<PostApiData>>
}
