package com.excaution.riwayaapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.data.post.PostGenre
import com.excaution.riwayaapp.presentation.home.PostFeedCard
import com.excaution.riwayaapp.presentation.theme.InkTheme
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SearchScreen() {
//        onCommentsClick: (PostResponse) -> Unit
    val viewModel: SearchViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val listState = rememberLazyListState()
    val verticalListState = rememberLazyListState()

        var searchQuery by remember { mutableStateOf("") }
        var selectedGenreFilter by remember { mutableStateOf<PostGenre?>(null) }

                var textInputState by remember { mutableStateOf("") }
                var chosenGenreFilter by remember { mutableStateOf<PostGenre?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(InkTheme.colors.bgDeep)
                ) {
                    // 1. Search Bar Field Input

                    SearchBar(
                        query = textInputState,
                        onQueryChanged = {
                            textInputState = it
                            viewModel.onQueryChanged(it) }
                    )

                    // 2. Horizontal Genre Filter Chips Row Bar Layout
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All" Reset Option Chip item
                        item {
                            FilterChip(
                                selected = chosenGenreFilter == null,
                                onClick = {
                                    chosenGenreFilter = null
                                    viewModel.onGenreChanged(null)
                                },
                                label = { Text("All Categories") }
                            )
                        }

                        // Loop and draw all active genre definitions from your backend Enum
                        items(PostGenre.entries.toTypedArray()) { genreItem ->
                            FilterChip(
                                selected = chosenGenreFilter == genreItem,
                                onClick = {
                                    chosenGenreFilter = genreItem
                                    viewModel.onGenreChanged(genreItem) // Changes query genre constraint parameters cleanly
                                },
                                label = { Text(genreItem.name.replace("_", " ").lowercase().capitalize()) }
                            )
                        }
                    }

                    // 3. Search Results Content Box
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val state = uiState) {
                            is PostUiState.Idle -> {
                                Text("Type above to discover world timelines.", color = InkTheme.colors.textMuted)
                            }
                            is PostUiState.Loading -> {
                                CircularProgressIndicator(color = InkTheme.colors.accentPrimary)
                            }
                            is PostUiState.SearchSuccess -> {
                                val searchItems = state.searchResult.content

                                if (searchItems.isEmpty()) {
                                    Text("No matching results found.", color = InkTheme.colors.textMuted)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        state = verticalListState,
                                        contentPadding = PaddingValues(bottom = 24.dp)
                                    ) {
                                        itemsIndexed(
                                            items = searchItems,
                                            key = { _, post -> post.id.toString() }
                                        ) { index, postItem ->

                                            // Pagination trigger hook for infinite search results loading
                                            if (index >= searchItems.lastIndex - 4) {
                                                viewModel.loadNextSearchPage()
                                            }

                                            PostFeedCard(
                                                post = postItem,
                                                modifier = Modifier,
                                                onCommentsClick = {
                                                    //onCommentsClick(postItem)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            is PostUiState.Error -> {
                                Text("Error: ${state.message}", color = Color.Red)
                            }
                            else -> {}
                        }
                    }
                }
            }
