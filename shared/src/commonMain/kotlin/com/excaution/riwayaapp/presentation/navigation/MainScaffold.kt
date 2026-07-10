package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.excaution.riwayaapp.domain.model.SampleData
import com.excaution.riwayaapp.domain.model.Story
import com.excaution.riwayaapp.domain.model.StoryGenre
import com.excaution.riwayaapp.presentation.books.BookstoreScreen
import com.excaution.riwayaapp.presentation.components.GenreTag
import com.excaution.riwayaapp.presentation.components.InitialsAvatar
import com.excaution.riwayaapp.presentation.components.PressScaleButton
import com.excaution.riwayaapp.presentation.components.SectionHeader
import com.excaution.riwayaapp.presentation.components.StatChip
import com.excaution.riwayaapp.presentation.components.SurfaceIconButton
import com.excaution.riwayaapp.presentation.navigation.bottombar.BottomViewModel
import com.excaution.riwayaapp.presentation.navigation.bottombar.FloatingBottomBar
import com.excaution.riwayaapp.presentation.notifications.NotificationScreen
import com.excaution.riwayaapp.presentation.profile.ProfileScreen
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgSurface
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import com.excaution.riwayaapp.presentation.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun MainScaffold(
    onLogout: () -> Unit
) {
    val bottomVM : BottomViewModel = viewModel()
    val bottomState by bottomVM.uiState.collectAsState()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    
    Scaffold(
        containerColor = InkTheme.colors.bgDeep,
        bottomBar = {
            FloatingBottomBar(
                selectedIndex = bottomState.selectedBottomIndex,
                onItemSelected = bottomVM::selectBottom,
                onSearchClick = {},
                modifier = Modifier,
                onHomeClick = {navController.navigate(Route.Main.Home) },
                onBookShopClick = {navController.navigate(Route.Main.BookShop)},
                onSavedClick = {navController.navigate(Route.Main.Saved)},
                onProfileClick = {navController.navigate(Route.Main.Profile)}
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Main.Home,
            modifier = Modifier.padding(innerPadding),
            enterTransition = NavAnimations.tabEnter,
            exitTransition = NavAnimations.tabExit,
        ) {
            composable<Route.Main.Home> { HomeContent(
                onNotificationClick = {navController.navigate(Route.Main.Notifications)}
            )}
            composable<Route.Main.BookShop> { BookstoreScreen() }
            composable<Route.Main.Profile> { ProfileScreen(onLogout = onLogout)}
            composable<Route.Main.Saved> { }

            composable<Route.Main.Notifications>(
                enterTransition = NavAnimations.slideUpEnter,
                exitTransition = NavAnimations.slideDownExit,
                popEnterTransition = NavAnimations.slideUpEnter,
                popExitTransition = NavAnimations.slideDownExit,
            ) {
                NotificationScreen()
            }
        }
    }
}

//-----------------------------------------------HomeContent----------------------------------
@Composable
fun HomeContent(onNotificationClick: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    var selectedGenre by remember { mutableStateOf(StoryGenre.ALL) }

    val filteredStories = remember(selectedGenre) {
        if (selectedGenre == StoryGenre.ALL) SampleData.stories
        else SampleData.stories.filter { it.genre == selectedGenre }
    }

    val featuredStory = SampleData.stories.first { it.isFeatured }

    // Stable list state — critical for smooth scroll
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(InkTheme.colors.bgDeep),
    ) {
        item(key = "topBar") { HomeTopBar(
            onNotificationClick = {onNotificationClick()}
        ) }
        item(key = "space") { Spacer(Modifier.height(4.dp)) }
        item(key = "search") { SearchBar() }
        item(key = "space1") { Spacer(Modifier.height(4.dp)) }
        item(key = "categories") { CategoryChips(selected = selectedGenre, onSelect = { selectedGenre = it }) }
        item(key = "sectionHeader") {
            SectionHeader(
                title = "Featured",
                actionLabel = "See all",
                onAction = {},
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
        item(key = "featuredStory") { FeaturedStoryCard(story = featuredStory, onClick = {}) }
        item(key = "sectionHeader1") {
            SectionHeader(
                title = "Trending Now",
                actionLabel = "See all",
                onAction = {},
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 22.dp,
                    bottom = 8.dp
                ),
            )
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
                Divider(
                    color     = InkTheme.colors.bgBorder.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(start = 90.dp, end = 20.dp),
                )
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(onNotificationClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        // Logo
        Text(
            text       = "RiwayaApp",
            fontSize   = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.8).sp,
            style = LocalTextStyle.current.copy(
                brush = Brush.linearGradient(GradientAccent),
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Notification bell
            SurfaceIconButton(
                icon = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                onClick = {onNotificationClick()},
                badge = 5,
            )
            // User avatar
            InitialsAvatar(initial = "A", size = 36.dp)
        }
    }
}

// ── Search Bar ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBar() {
    var query by remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InkTheme.colors.bgSurface)
            .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Icon(
            imageVector        = Icons.Rounded.Search,
            contentDescription = null,
            tint               = InkTheme.colors.textFaint,
            modifier           = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value         = query,
            onValueChange = { query = it },
            textStyle = LocalTextStyle.current.copy(
                color    = InkTheme.colors.textPrimary,
                fontSize = 14.sp,
            ),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search stories, authors…", fontSize = 14.sp, color = InkTheme.colors.textFaint)
                }
                inner()
            },
            modifier = Modifier.weight(1f),
        )
    }
}

// ── Category Chips ───────────────────────────────────────────────────────────

@Composable
private fun CategoryChips(
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
private fun CategoryChip(
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
private fun AnimatedStoryListItem(
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








