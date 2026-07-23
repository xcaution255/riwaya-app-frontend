package com.excaution.riwayaapp.data.post


import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
enum class PostGenre {
    ALL, STORIES, ENTERTAINMENT, ARTICLES, SPORTS, MOVIES
}

@Serializable
data class LikeResponse(val liked: Boolean, val likeCount: Long)

@Serializable
data class SaveResponse(val saved: Boolean, val saveCount: Long)
@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val genre: PostGenre
)

@Serializable
data class UpdatePostRequest(
    val content: String? = null
)

@Serializable
data class PostResponse(
    val id: Uuid,
    val authorId: Uuid,
    val authorName: String?,
    val title: String,
    val content: String,
    val genre: PostGenre,
    val imageUrl: String?,
    val likeCount: Long,
    val saveCount: Long,
    val viewCount: Long,
    val commentCount: Long,
    val likedByCurrentUser: Boolean,
    val savedByCurrentUser: Boolean,
    val isOwnPost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant?
)

@Serializable
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)


