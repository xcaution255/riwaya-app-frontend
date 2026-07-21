package com.excaution.riwayaapp.presentation.home


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.excaution.riwayaapp.domain.model.StoryGenreFeed
import com.excaution.riwayaapp.presentation.components.SurfaceIconButton
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Suppress("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(onNotificationClick: () -> Unit) {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    var showSheet    by remember { mutableStateOf(false) }
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val viewModel : PostViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    // 1. Collect the pagination state at the top of HomeScreen
    val isPaginationLoading by viewModel.isPaginationLoading.collectAsState()
    // 1. Calculate the active refreshing state on the fly based on current UI flags
    val isRefreshing = uiState is PostUiState.Loading && listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0


    LaunchedEffect(Unit) {
        viewModel.loadNextPage()
    }
    //for chips
    var selectedGenre by remember { mutableStateOf(StoryGenreFeed.ALL) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Crucial: Connects scroll to TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "RiwayaApp",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp,
                        style = LocalTextStyle.current.copy(
                            brush = Brush.linearGradient(GradientAccent),
                        ),
                    )
                },
                actions = {
                    SurfaceIconButton(
                        icon = Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        onClick = {onNotificationClick()},
                        badge = 5,
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0), //added
                scrollBehavior = scrollBehavior, // Passes scroll behavior down
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InkTheme.colors.bgDeep,
                    scrolledContainerColor = InkTheme.colors.bgDeep
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InkTheme.colors.bgDeep),
            contentAlignment = Alignment.Center
        ) {
            when(val state = uiState) {
                is PostUiState.Idle -> {}
                // Show the full screen loader indicator ONLY on the absolute first page load
                is PostUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is PostUiState.FeedSuccess -> {
                    // 1. Filter the live network data using the selected genre chip
                    val liveFilteredPosts = remember(selectedGenre, state.pageData.content) {
                        if (selectedGenre == StoryGenreFeed.ALL) {
                            state.pageData.content
                        } else {
                            // Converts response genre to match your UI chip Enum if named exactly the same
                            state.pageData.content.filter { it.genre.name == selectedGenre.name }
                        }
                    }
                    // 2. Wrap your layout container here to monitor swipe refresh gestures
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            // Clears pagination counters and pulls a fresh page 0 branch
                            viewModel.refreshFeed()
                        }
                    ){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 0.dp),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding(), // Handles top bar dynamically
                            bottom = innerPadding.calculateBottomPadding()
                        )
                    ) {
                        // When the TopAppBar hides, this layer slides up and locks perfectly at the very top.
                        // Sticky genre selector header
                        stickyHeader {
                            CategoryChips(
                                selected = selectedGenre,
                                onSelect = { selectedGenre = it })
                        }

                        // 2. Loop through the real network data list instead of the mock list
                        itemsIndexed(
                            items = liveFilteredPosts,
                            key = { _, post -> post.id.toString() } // Safe multiplatform UUID string conversion
                        ) { index, postItem -> // 'postItem' represents a single PostResponse object

                            // 2. Pagination Trigger Hook: If the user scrolls 4 items away from the bottom, pre-fetch next page!
                            if (index >= liveFilteredPosts.lastIndex - 4) {
                                viewModel.loadNextPage()
                            }
                            PostFeedCard(
                                post = postItem, //  FIXED: Pass the individual item, not the whole list!
                                modifier = Modifier,
                                onCommentsClick = { clickedPost ->
                                    // You can track which post comments to open here if needed
                                    showSheet = true
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }
                        // 2. Append the bottom pagination indicator item conditionally
                        if (isPaginationLoading) {
                            item(key = "pagination-loader") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = InkTheme.colors.accentPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        } else {
                            // Fallback standard spacing item when not loading data
                            item(key = "bottom-spacer") {
                                Spacer(Modifier.height(40.dp))
                            }
                        }
                    }
                }
                }
                is PostUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                } else -> {}
            }
        }
    }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState       = sheetState,
            containerColor   = InkTheme.colors.bgSurface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(InkTheme.colors.bgBorder),
                )
            },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
          CommentsSheet(
              totalComments = 5345,
              onDismiss = {scope.launch { sheetState.hide() }
                  showSheet = false}
          )
        }
    }
}

@Composable
fun CategoryChips( //private
    selected: StoryGenreFeed,
    onSelect: (StoryGenreFeed) -> Unit,
) {
    val genres = StoryGenreFeed.values()
    val genreEmojis = mapOf(
        StoryGenreFeed.ALL       to "",
        StoryGenreFeed.STORIES   to "",
        StoryGenreFeed.ENTERTAINMENT   to "",
        StoryGenreFeed.ARTICLES   to "",
        StoryGenreFeed.SPORTS     to "",
        StoryGenreFeed.MOVIES    to ""
    )
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier              = Modifier.padding(vertical = 10.dp),
    ) {
        items(genres) { genre ->
            CategoryChip(
                label = "${genreEmojis[genre]} ${genre.label}",
                isActive = genre == selected,
                onClick = { onSelect(genre) },
            )
        }
    }
}

@Composable
fun CategoryChip( //private
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue   = if (isActive) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "chipScale",
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isActive) Modifier.background(Brush.linearGradient(GradientAccent))
                else Modifier.background(InkTheme.colors.bgSurface)
                    .border(1.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(20.dp))
            )
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {onClick()}
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            text       = label,
            style = InkTheme.typography.bodyMedium,
            color      = if (isActive) Color.White else InkTheme.colors.textSecondary,
        )
    }
}
