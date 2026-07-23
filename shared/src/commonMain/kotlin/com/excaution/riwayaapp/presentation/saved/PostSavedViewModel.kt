package com.excaution.riwayaapp.presentation.saved

import com.excaution.riwayaapp.data.post.PageResponse
import com.excaution.riwayaapp.data.post.PostGenre
import com.excaution.riwayaapp.data.post.PostResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.data.post.PostRepository
import com.excaution.riwayaapp.presentation.home.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class PostSavedViewModel(
    private val repository: PostRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostSavedUiState>(PostSavedUiState.Idle)
    val uiState: StateFlow<PostSavedUiState> = _uiState.asStateFlow()

    private val _isPaginationLoading = MutableStateFlow(false)
    val isPaginationLoading: StateFlow<Boolean> = _isPaginationLoading.asStateFlow()

    // Pagination State Tracking Variables
    private var currentPage = 0
    private var isLastPage = false
    private var isCurrentlyLoading = false
    private val accumulatedPosts = mutableListOf<PostResponse>()

    // Core Pagination Fetcher Engine
//    fun loadNextPage() = viewModelScope.launch {
//        if (isCurrentlyLoading || isLastPage) return@launch
//        isCurrentlyLoading = true
//
//        // Only flash the full-screen spinner on the first initial load
//        if (currentPage == 0) {
//            _uiState.value = PostSavedUiState.Loading
//        } else {
//            // Toggle bottom pagination loader row on for subsequent pages
//            _isPaginationLoading.value = true
//        }
//
//        when (val result = repository.getSavedPosts(page = currentPage, size = 20)) {
//            is ApiResult.Success -> {
//                val pageData = result.data
//                accumulatedPosts.addAll(pageData.content)
//                isLastPage = !pageData.hasNext
//
//                _uiState.value = PostSavedUiState.FeedSuccess(
//                    pageData = pageData.copy(content = accumulatedPosts.toList())
//                )
//                currentPage++
//            }
//            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
//            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
//        }
//
//        // Turn off loaders
//        isCurrentlyLoading = false
//        _isPaginationLoading.value = false
//    }
//
//    // Call this if the user pulls down to refresh or changes genre filtering categories
//    // Inside your refreshFeed() function, reset the flag:
//    fun refreshFeed() {
//        currentPage = 0
//        isLastPage = false
//        isCurrentlyLoading = false
//        _isPaginationLoading.value = false
//        accumulatedPosts.clear() // Wipes clean the current list items before page zero arrives
//        loadNextPage()
//    }

    private var currentGenreFilter: PostGenre? = null

    // Modified infinite scroll engine method signature
    fun loadNextPage(genre: PostGenre? = currentGenreFilter) = viewModelScope.launch {
        if (isCurrentlyLoading || isLastPage) return@launch
        isCurrentlyLoading = true

        // Detect if the user changed genre categories to clear memory immediately
        if (genre != currentGenreFilter) {
            currentGenreFilter = genre
            currentPage = 0
            isLastPage = false
            accumulatedPosts.clear() //wipe complete the previous list items
            _uiState.value = PostSavedUiState.Loading // Flash main spinner loader row
        }

        if (currentPage == 0 && _uiState.value !is PostSavedUiState.Loading) {
            _uiState.value = PostSavedUiState.Loading
        } else if (currentPage > 0) {
            _isPaginationLoading.value = true
        }

        // Pass the active genre state target down to Ktor client requests
        when (val result = repository.getFeed(genre = currentGenreFilter, page = currentPage, size = 20)) {
            is ApiResult.Success -> {
                val pageData = result.data
                accumulatedPosts.addAll(pageData.content)
                isLastPage = !pageData.hasNext

                _uiState.value = PostSavedUiState.FeedSuccess(
                    pageData = pageData.copy(content = accumulatedPosts.toList())
                )
                currentPage++
            }
            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
        isCurrentlyLoading = false
        _isPaginationLoading.value = false
    }

    // Modify refresh implementation helper
    fun refreshFeed() {
        currentPage = 0
        isLastPage = false
        isCurrentlyLoading = false
        _isPaginationLoading.value = false
        accumulatedPosts.clear()
        loadNextPage(currentGenreFilter) // Re-fetch the currently selected genre
    }



    //like and save post
    fun likePost(postId: Uuid) =
        viewModelScope.launch {
            repository.likePost(postId)
        }

    fun savePost(postId: Uuid) =
        viewModelScope.launch {
            repository.savePost(postId)
        }

    // 1. Fetch Global Timeline Feed
    fun getFeed(page: Int = 0, size: Int = 20, genre: PostGenre?) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.getFeed(page, size, genre)) {
            is ApiResult.Success -> _uiState.value = PostSavedUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // 2. Create a New Post Entry
    fun createPost(title: String, content: String, genre: PostGenre, imageUrl: String? = null) =
        viewModelScope.launch {
            _uiState.value = PostSavedUiState.Loading
            when (val result = repository.createPost(title, content, genre, imageUrl)) {
                is ApiResult.Success -> {
                    _uiState.value = PostSavedUiState.SinglePostSuccess(post = result.data)
                }

                is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
                ApiResult.NetworkError -> _uiState.value =
                    PostSavedUiState.Error("No internet connection")
            }
        }

    // 3. Fetch Specific Detailed Post Item
    fun getPost(postId: Uuid) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.getPost(postId)) {
            is ApiResult.Success -> _uiState.value =
                PostSavedUiState.SinglePostSuccess(post = result.data)

            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // 4. Update an Existing Post Entry
    fun updatePost(postId: Uuid, content: String) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.updatePost(postId, content)) {
            is ApiResult.Success -> _uiState.value =
                PostSavedUiState.SinglePostSuccess(post = result.data)

            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // 5. Delete or Remove Post Entry
    fun deletePost(postId: Uuid) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.deletePost(postId)) {
            is ApiResult.Success -> {
                _uiState.value = PostSavedUiState.ActionSuccess(result.data["message"] ?: "Post removed")
            }

            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }


    // 8. Fetch Saved Bookmarked Timeline Lists
    fun getSavedPosts(page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.getSavedPosts(page, size)) {
            is ApiResult.Success -> _uiState.value = PostSavedUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // 9. Fetch Liked Timeline Lists
    fun getLikedPosts(page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.getLikedPosts(page, size)) {
            is ApiResult.Success -> _uiState.value = PostSavedUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // 10. Fetch Filtered Sub-User Specific Timeline Updates
    fun getUserPosts(authorId: Uuid, page: Int = 0, size: Int = 20) = viewModelScope.launch {
        _uiState.value = PostSavedUiState.Loading
        when (val result = repository.getUserPosts(authorId, page, size)) {
            is ApiResult.Success -> _uiState.value = PostSavedUiState.FeedSuccess(pageData = result.data)
            is ApiResult.Error -> _uiState.value = PostSavedUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostSavedUiState.Error("No internet connection")
        }
    }

    // Comprehensive session timeout security handler
    private suspend fun handleNetworkError(code: Int, fallbackMessage: String) {
        if (code == 401 || code == 403) {
            tokenStorage.clearTokens()
            _uiState.value = PostSavedUiState.Error("Session expired. Please log in again.")
        } else {
            _uiState.value = PostSavedUiState.Error(fallbackMessage)
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


sealed interface PostSavedUiState {
    object Idle : PostSavedUiState
    object Loading : PostSavedUiState
    data class FeedSuccess(val pageData: PageResponse<PostResponse>) : PostSavedUiState
    data class SinglePostSuccess(val post: PostResponse) : PostSavedUiState
    data class ActionSuccess(val message: String) : PostSavedUiState
    data class Error(val message: String) : PostSavedUiState
}


