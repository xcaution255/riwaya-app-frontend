package com.excaution.riwayaapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.data.post.PageResponse
import com.excaution.riwayaapp.data.post.PostGenre
import com.excaution.riwayaapp.data.post.PostRepository
import com.excaution.riwayaapp.data.post.PostResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    // Internal state flows to observe input changes for debouncing
    private val _searchQuery = MutableStateFlow("")
    private val _selectedGenre = MutableStateFlow<PostGenre?>(null)

    private var currentPage = 0
    private var isLastPage = false
    private var isCurrentlyLoading = false
    private val accumulatedResults = mutableListOf<PostResponse>()

    private var currentQuery = ""
    private var currentGenre: PostGenre? = null

    init {
        // Combine query text and genre selections into a unified debounced stream pipeline
        combine(_searchQuery, _selectedGenre) { query, genre -> Pair(query, genre) }
            .debounce(500) //  Wait 500ms for user to pause typing
            .distinctUntilChanged() // Skip redundant identical parameters
            .onEach { (query, genre) ->
                if (query.isBlank()) {
                    _uiState.value = PostUiState.Idle
                    accumulatedResults.clear()
                } else {
                    // Trigger a completely fresh query whenever a parameter modifies
                    resetAndSearch(query, genre)
                }
            }
            .launchIn(viewModelScope)
    }


    // Public inputs triggered by your Compose views
    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onGenreChanged(newGenre: PostGenre?) {
        _selectedGenre.value = newGenre
    }

    private fun resetAndSearch(query: String, genre: PostGenre?) {
        currentPage = 0
        isLastPage = false
        isCurrentlyLoading = false
        accumulatedResults.clear()
        executeSearchRequest(query, genre)
    }

    // Handles subsequent scrolling fetches
    fun loadNextSearchPage() {
        if (isCurrentlyLoading || isLastPage || _searchQuery.value.isBlank()) return
        executeSearchRequest(_searchQuery.value, _selectedGenre.value)
    }

    private fun executeSearchRequest(query: String, genre: PostGenre?) = viewModelScope.launch {
        if (isCurrentlyLoading) return@launch
        isCurrentlyLoading = true

        if (currentPage == 0) {
            _uiState.value = PostUiState.Loading
        }

        when (val result = repository.searchPosts(query, genre, currentPage, 20)) {
            is ApiResult.Success -> {
                val pageData = result.data
                accumulatedResults.addAll(pageData.content)
                isLastPage = !pageData.hasNext

                _uiState.value = PostUiState.SearchSuccess(
                    searchResult = pageData.copy(content = accumulatedResults.toList())
                )
                currentPage++
            }
            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
        }
        isCurrentlyLoading = false
    }

    // Executes a brand new search string execution query execution tree
    fun executeNewSearch(query: String, genre: PostGenre? = null) {
        if (query.isBlank()) {
            _uiState.value = PostUiState.Idle
            return
        }
        currentQuery = query
        currentGenre = genre
        currentPage = 0
        isLastPage = false
        isCurrentlyLoading = false
        accumulatedResults.clear()

        loadNextSearchPage()
    }

    // Handles infinite scrolling loading bars inside search result scrolling columns
//    fun loadNextSearchPage() = viewModelScope.launch {
//        if (isCurrentlyLoading || isLastPage) return@launch
//        isCurrentlyLoading = true
//
//        if (currentPage == 0) {
//            _uiState.value = PostUiState.Loading
//        }
//
//        when (val result = repository.searchPosts(currentQuery, currentGenre, currentPage, 20)) {
//            is ApiResult.Success -> {
//                val pageData = result.data
//                accumulatedResults.addAll(pageData.content)
//                isLastPage = !pageData.hasNext
//
//                _uiState.value = PostUiState.SearchSuccess(
//                    searchResult = pageData.copy(content = accumulatedResults.toList())
//                )
//                currentPage++
//            }
//            is ApiResult.Error -> _uiState.value = PostUiState.Error(message = result.message)
//            ApiResult.NetworkError -> _uiState.value = PostUiState.Error("No internet connection")
//        }
//        isCurrentlyLoading = false
//    }
}

sealed interface PostUiState {
    object Idle : PostUiState
    object Loading : PostUiState
    data class SearchSuccess(val searchResult: PageResponse<PostResponse>) : PostUiState
    data class FeedSuccess(val pageData: PageResponse<PostResponse>) : PostUiState
    data class SinglePostSuccess(val post: PostResponse) : PostUiState
    data class ActionSuccess(val message: String) : PostUiState
    data class Error(val message: String) : PostUiState
}
