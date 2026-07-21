package com.excaution.riwayaapp.data.post


import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.network.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import kotlin.uuid.Uuid

class PostApi(private val client: HttpClient) {

    suspend fun createPost(request: CreatePostRequest): ApiResult<PostResponse> = safeApiCall {
        client.post("/posts") {
            setBody(request)
        }.body()
    }

    suspend fun getPost(postId: Uuid): ApiResult<PostResponse> = safeApiCall {
        client.get("/posts/$postId").body()
    }

    suspend fun getFeed(page: Int, size: Int): ApiResult<PageResponse<PostResponse>> = safeApiCall {
        client.get("/posts/feed") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getUserPosts(authorId: Uuid, page: Int, size: Int): ApiResult<PageResponse<PostResponse>> = safeApiCall {
        client.get("/posts/user/$authorId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getSavedPosts(page: Int, size: Int): ApiResult<PageResponse<PostResponse>> = safeApiCall {
        client.get("/posts/saved") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getLikedPosts(page: Int, size: Int): ApiResult<PageResponse<PostResponse>> = safeApiCall {
        client.get("/posts/liked") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun updatePost(postId: Uuid, request: UpdatePostRequest): ApiResult<PostResponse> = safeApiCall {
        client.put("/posts/$postId") {
            setBody(request)
        }.body()
    }

    suspend fun deletePost(postId: Uuid): ApiResult<Map<String, String>> = safeApiCall {
        client.delete("/posts/$postId").body()
    }
}

