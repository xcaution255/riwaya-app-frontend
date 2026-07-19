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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Notifications
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.excaution.riwayaapp.domain.model.SampleData.storyFeed
import com.excaution.riwayaapp.domain.model.StoryGenreFeed
import com.excaution.riwayaapp.presentation.components.SurfaceIconButton
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(onNotificationClick: () -> Unit) {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    var showSheet    by remember { mutableStateOf(false) }
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    //for chips
    var selectedGenre by remember { mutableStateOf(StoryGenreFeed.ALL) }
    val filteredStories = remember(selectedGenre) {
        if (selectedGenre == StoryGenreFeed.ALL) storyFeed
        else storyFeed.filter { it.genre == selectedGenre }
    }

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
            stickyHeader {
                CategoryChips(selected = selectedGenre, onSelect = { selectedGenre = it })
            }

           itemsIndexed(
                items = filteredStories.filter { !it.isFeatured },
                key   = { _, s -> s.id },
            ) { index, item ->
                StoryFeedCard(
                    item = item,
                    modifier = Modifier,
                    onCommentsClick = {  showSheet = true
                        scope.launch { sheetState.show() }}
                )
           }
            item(key = "bottom-spacer") { Spacer(Modifier.height(40.dp)) }
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
