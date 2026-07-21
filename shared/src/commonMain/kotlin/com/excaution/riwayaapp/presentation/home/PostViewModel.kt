package com.excaution.riwayaapp.presentation.home

import com.excaution.riwayaapp.data.post.PageResponse
import com.excaution.riwayaapp.data.post.PostGenre
import com.excaution.riwayaapp.data.post.PostResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.data.post.PostRepository
import com.excaution.riwayaapp.presentation.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class PostViewModel(
    private val repository: PostRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val _isPaginationLoading = MutableStateFlow(false)
    val isPaginationLoading: StateFlow<Boolean> = _isPaginationLoading.asStateFlow()

    // Pagination State Tracking Variables
    private var currentPage = 0
    private var isLastPage = false
    private var isCurrentlyLoading = false
    private val accumulatedPosts = mutableListOf<PostResponse>()

    // Core Pagination Fetcher Engine
    fun loadNextPage() = viewModelScope.launch {
        if (isCurrentlyLoading || isLastPage) return@launch
        isCurrentlyLoading = true

        // Only flash the full-screen spinner on the first initial load
        if (currentPage == 0) {
            _uiState.value = PostUiState.Loading
        } else {
            // Toggle bottom pagination loader row on for subsequent pages
            _isPaginationLoading.value = true
        }

        when (val result = repository.getFeed(page = currentPage, size = 20)) {
            is ApiResult.Success -> {
                val pageData = result.data
                accumulatedPosts.addAll(pageData.content)
                isLastPage = !pageData.hasNext

                _uiState.value = PostUiState.FeedSuccess(
                    pageData = pageData.copy(content = accumulatedPosts.toList())
                )
                currentPage++
            }
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }

        // Turn off loaders
        isCurrentlyLoading = false
        _isPaginationLoading.value = false
    }

    // Call this if the user pulls down to refresh or changes genre filtering categories
    // Inside your refreshFeed() function, reset the flag:
    fun refreshFeed() {
        currentPage = 0
        isLastPage = false
        isCurrentlyLoading = false
        _isPaginationLoading.value = false
        accumulatedPosts.clear() // Wipes clean the current list items before page zero arrives
        loadNextPage()
    }

    // 1. Fetch Global Timeline Feed
    fun getFeed(page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.getFeed(page, size)) {
            is ApiResult.Success -> _uiState.value = PostUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // 2. Create a New Post Entry
    fun createPost(title: String, content: String, genre: PostGenre, imageUrl: String? = null) =
        viewModelScope.launch {
            _uiState.value = PostUiState.Loading
            when (val result = repository.createPost(title, content, genre, imageUrl)) {
                is ApiResult.Success -> {
                    _uiState.value = PostUiState.SinglePostSuccess(post = result.data)
                }

                is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
                ApiResult.NetworkError -> _uiState.value =
                    PostUiState.Error("No internet connection")
            }
        }

    // 3. Fetch Specific Detailed Post Item
    fun getPost(postId: Uuid) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.getPost(postId)) {
            is ApiResult.Success -> _uiState.value =
                PostUiState.SinglePostSuccess(post = result.data)

            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // 4. Update an Existing Post Entry
    fun updatePost(postId: Uuid, content: String) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.updatePost(postId, content)) {
            is ApiResult.Success -> _uiState.value =
                PostUiState.SinglePostSuccess(post = result.data)

            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // 5. Delete or Remove Post Entry
    fun deletePost(postId: Uuid) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.deletePost(postId)) {
            is ApiResult.Success -> {
                _uiState.value = PostUiState.ActionSuccess(result.data["message"] ?: "Post removed")
            }

            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }


    // 8. Fetch Saved Bookmarked Timeline Lists
    fun getSavedPosts(page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.getSavedPosts(page, size)) {
            is ApiResult.Success -> _uiState.value = PostUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // 9. Fetch Liked Timeline Lists
    fun getLikedPosts(page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.getLikedPosts(page, size)) {
            is ApiResult.Success -> _uiState.value = PostUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // 10. Fetch Filtered Sub-User Specific Timeline Updates
    fun getUserPosts(authorId: Uuid, page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostUiState.Loading
        when (val result = repository.getUserPosts(authorId, page, size)) {
            is ApiResult.Success -> _uiState.value = PostUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
    }

    // Comprehensive session timeout security handler
    private suspend fun handleNetworkError(code: Int, fallbackMessage: String) {
        if (code == 401 || code == 403) {
            tokenStorage.clearTokens()
            _uiState.value = PostUiState.Error("Session expired. Please log in again.")
        } else {
            _uiState.value = PostUiState.Error(fallbackMessage)
        }
    }


    fun formatTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "1s"
        return try {
            val instant = Instant.parse(isoString)
            val now = Clock.System.now()
            val duration = now - instant

            val seconds = duration.inWholeSeconds
            val minutes = duration.inWholeMinutes
            val hours = duration.inWholeHours
            val days = duration.inWholeDays

            when {
                seconds < 60 -> "${maxOf(1, seconds)}s"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 30 -> "${days}d ago"
                days < 365 -> "${days / 30}month ago" // Outputs month abbreviation (e.g. 1m)
                else -> "${days / 365}y ago" // Outputs year abbreviation (e.g. 6y)
            }
        } catch (e: Exception) {
            "1s"
        }
    }
}


sealed interface PostUiState {
    object Idle : PostUiState
    object Loading : PostUiState
    data class FeedSuccess(val pageData: PageResponse<PostResponse>) : PostUiState
    data class SinglePostSuccess(val post: PostResponse) : PostUiState
    data class ActionSuccess(val message: String) : PostUiState
    data class Error(val message: String) : PostUiState
}


