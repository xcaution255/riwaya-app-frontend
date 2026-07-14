package com.excaution.riwayaapp.presentation.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import com.excaution.riwayaapp.domain.model.SampleData
import com.excaution.riwayaapp.domain.model.Story
import com.excaution.riwayaapp.domain.model.StoryGenreFeed
import com.excaution.riwayaapp.presentation.components.GenreTag
import com.excaution.riwayaapp.presentation.components.StatChip
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedScreen() {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    //for chips
    var selectedGenre by remember { mutableStateOf(StoryGenreFeed.ALL) }
    val filteredStories = remember(selectedGenre) {
        if (selectedGenre == StoryGenreFeed.ALL) SampleData.stories
        else SampleData.stories.filter { it.genre == selectedGenre }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Crucial: Connects scroll to TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Saved",
                        fontSize   = 24.sp,
                        letterSpacing = (-0.8).sp,
                        style = LocalTextStyle.current.copy(
                            brush = Brush.linearGradient(GradientAccent),
                        ),
                    )
                },
                actions = {},
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
            ) { index, story ->
                AnimatedStoryListItem(
                    story   = story,
                    index   = index,
                    onClick = {},
                )
                if (index < filteredStories.size - 2) {
                    HorizontalDivider(
                        color     = InkTheme.colors.bgBorder.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier  = Modifier.padding(start = 90.dp, end = 20.dp),
                    )
                }
            }
        }
    }
}

// ── Category Chips ───────────────────────────────────────────────────────────

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
                label    = "${genreEmojis[genre]} ${genre.label}",
                isActive = genre == selected,
                onClick  = { onSelect(genre) },
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
        label = "chipScale",
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
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null){onClick()}
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            text       = label,
            style = InkTheme.typography.bodyMedium,
            color      = if (isActive) Color.White else InkTheme.colors.textSecondary,
        )
    }
}


// ── Story List Item ───────────────────────────────────────────────────────────
@Composable
fun AnimatedStoryListItem( //private
    story: Story,
    index: Int,
    onClick: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(story.id) {
        delay(index * 60L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -20 },
    ) {
        StoryListItem(story = story, onClick = onClick)
    }
}

@Composable
private fun StoryListItem(story: Story, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null){onClick()}
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        // Cover
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(58.dp)
                .height(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(story.coverGradient)),
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(story.coverGradient.first().copy(alpha = 0.6f))
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(story.coverGradient.first().copy(alpha = 0.8f))
                    .border(1.dp, story.genre.color.copy(alpha = 0.5f), CircleShape),
            ) {
                Text(story.coverEmoji, style = InkTheme.typography.bodyLarge)
            }
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            GenreTag(label = story.genre.label, color = story.genre.color)
            Spacer(Modifier.height(2.dp))
            Text(
                text = story.title,
                style = InkTheme.typography.titleLarge,
                color = InkTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text     = "by ${story.author}",
                style = InkTheme.typography.bodySmall,
                color    = InkTheme.colors.textMuted,
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("♥", story.likes)
                StatChip("💬", story.comments)
                StatChip("⏱", "${story.readTimeMin} min")
            }
        }

        // Chevron
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(InkTheme.colors.bgSurface)
                .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(10.dp)),
        ) {
            Icon(
                imageVector        = Icons.Rounded.ChevronRight,
                contentDescription = "Open story",
                tint               = InkTheme.colors.accentPrimary,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}