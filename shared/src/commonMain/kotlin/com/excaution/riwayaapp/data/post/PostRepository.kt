package com.excaution.riwayaapp.data.post


import com.excaution.riwayaapp.core.network.ApiResult
import kotlin.uuid.Uuid

class PostRepository(private val api: PostApi) {

    suspend fun createPost(title: String, content: String, genre: PostGenre, imageUrl: String? = null): ApiResult<PostResponse> {
        return api.createPost(CreatePostRequest(title, content, imageUrl, genre))
    }

    suspend fun getPost(postId: Uuid): ApiResult<PostResponse> = api.getPost(postId)

    suspend fun getFeed(page: Int = 0, size: Int = 20): ApiResult<PageResponse<PostResponse>> = api.getFeed(page, size)

    suspend fun getUserPosts(authorId: Uuid, page: Int = 0, size: Int = 20): ApiResult<PageResponse<PostResponse>> = api.getUserPosts(authorId, page, size)

    suspend fun getSavedPosts(page: Int = 0, size: Int = 20): ApiResult<PageResponse<PostResponse>> = api.getSavedPosts(page, size)

    suspend fun getLikedPosts(page: Int = 0, size: Int = 20): ApiResult<PageResponse<PostResponse>> = api.getLikedPosts(page, size)

    suspend fun updatePost(postId: Uuid, content: String): ApiResult<PostResponse> {
        return api.updatePost(postId, UpdatePostRequest(content))
    }

    suspend fun deletePost(postId: Uuid): ApiResult<Map<String, String>> = api.deletePost(postId)


}
