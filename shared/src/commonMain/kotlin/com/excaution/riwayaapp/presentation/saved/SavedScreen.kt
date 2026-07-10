package com.excaution.riwayaapp.presentation.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.domain.model.SampleData
import com.excaution.riwayaapp.domain.model.Story
import com.excaution.riwayaapp.domain.model.StoryGenre
import com.excaution.riwayaapp.presentation.components.GenreTag
import com.excaution.riwayaapp.presentation.components.InitialsAvatar
import com.excaution.riwayaapp.presentation.components.PressScaleButton
import com.excaution.riwayaapp.presentation.components.StatChip
import com.excaution.riwayaapp.presentation.components.SurfaceIconButton
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedScreen() {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    //for chips
    var selectedGenre by remember { mutableStateOf(StoryGenre.ALL) }
    val filteredStories = remember(selectedGenre) {
        if (selectedGenre == StoryGenre.ALL) SampleData.stories
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
                        fontWeight = FontWeight.ExtraBold,
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
    selected: StoryGenre,
    onSelect: (StoryGenre) -> Unit,
) {
    val genres = StoryGenre.values()
    val genreEmojis = mapOf(
        StoryGenre.ALL       to "✦",
        StoryGenre.FANTASY   to "🔮",
        StoryGenre.ROMANCE   to "💘",
        StoryGenre.MYSTERY   to "🔍",
        StoryGenre.SCIFI     to "🚀",
        StoryGenre.HORROR    to "😱",
        StoryGenre.ADVENTURE to "🌍",
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
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            text       = label,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isActive) Color.White else InkTheme.colors.textSecondary,
        )
    }
}

// ── Featured Story Card ───────────────────────────────────────────────────────

@Composable
private fun FeaturedStoryCard(story: Story, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 },
    ) {
        PressScaleButton(
            onClick = onClick,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(story.coverGradient)),
            ) {
                // Decorative glow circles
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .offset(x = (-40).dp, y = (-40).dp)
                        .clip(CircleShape)
                        .background(InkTheme.colors.accentPrimary.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(x = 220.dp, y = 80.dp)
                        .clip(CircleShape)
                        .background(InkTheme.colors.accentLight.copy(alpha = 0.1f))
                )

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000000))
                            )
                        )
                )

                // Editor's pick badge
                Box(
                    modifier = Modifier
                        .padding(14.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(InkTheme.colors.accentPrimary.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "✦ EDITOR'S PICK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.8.sp,
                    )
                }

                // Content at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    GenreTag(
                        label = story.genre.label,
                        color = story.genre.color,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = story.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.4).sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        InitialsAvatar(initial = story.authorInitial, size = 22.dp, fontSize = 9)
                        Text(
                            text = "by ${story.author}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Text("·", fontSize = 12.sp, color = InkTheme.colors.textMuted)
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = InkTheme.colors.textMuted,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(story.reads, fontSize = 11.sp, color = InkTheme.colors.textMuted)
                    }
                }
            }
        }
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
            .clickable(onClick = onClick)
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
                Text(story.coverEmoji, fontSize = 16.sp)
            }
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            GenreTag(label = story.genre.label, color = story.genre.color)
            Spacer(Modifier.height(2.dp))
            Text(
                text       = story.title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = InkTheme.colors.textPrimary,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                letterSpacing = (-0.2).sp,
            )
            Text(
                text     = "by ${story.author}",
                fontSize = 12.sp,
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