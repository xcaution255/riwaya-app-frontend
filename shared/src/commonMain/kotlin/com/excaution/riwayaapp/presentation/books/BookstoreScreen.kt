package com.excaution.riwayaapp.presentation.books

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.domain.model.Book
import com.excaution.riwayaapp.domain.model.SampleData
import com.excaution.riwayaapp.domain.model.StoryGenreFeed
import com.excaution.riwayaapp.format
import com.excaution.riwayaapp.presentation.components.GenreTag
import com.excaution.riwayaapp.presentation.components.PressScaleButton
import com.excaution.riwayaapp.presentation.components.StarRating
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.GradientFeatured
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookStoreScreen() {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var selectedGenre by remember { mutableStateOf<StoryGenreFeed?>(null) }
    //for chips
    val filteredBooks = remember(selectedGenre) {
        if (selectedGenre == null) SampleData.books
        else SampleData.books.filter { it.genre == selectedGenre }
    }

    // Stable list state — critical for smooth scroll
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Crucial: Connects scroll to TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Bookstore",
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
            state = listState,
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(), // Handles top bar dynamically
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            item(key = "banner") { PromoBanner(modifier = Modifier.padding(horizontal = 20.dp)) }
            item(key = "spacer") {Spacer(Modifier.height(8.dp))  }
            // When the TopAppBar hides, this layer slides up and locks perfectly at the very top.
            stickyHeader {
                FilterChips(selected = selectedGenre, onSelect = { selectedGenre = it })
            }
            item(key = "spacer1") {Spacer(Modifier.height(8.dp))  }
            item(key = "grid") {
                BooksGrid(
                    books       = filteredBooks.filter { !it.isBestseller },
                    onBookClick = {},
                    onAddToCart = { },
                )
            }
        }
    }
}
// ── Promo Banner ──────────────────────────────────────────────────────────────

@Composable
private fun PromoBanner(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(500)) + expandVertically(tween(500)),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(GradientFeatured))
                .border(1.dp, InkTheme.colors.accentPrimary.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(16.dp),
        ) {
            // Ambient glow blobs
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 240.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.12f))
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 200.dp, y = 30.dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentLight.copy(alpha = 0.1f))
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Book icon box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(GradientAccent)),
                ) {
                    Text("📚", fontSize = 22.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text          = "LIMITED OFFER",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = InkTheme.colors.accentPrimary,
                        letterSpacing = 0.8.sp,
                    )
                    Text(
                        text          = "Summer Reading Sale",
                        fontSize      = 15.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = InkTheme.colors.textPrimary,
                        letterSpacing = (-0.3).sp,
                    )
                    Text(
                        text     = "Up to 40% off bestsellers this week",
                        fontSize = 11.sp,
                        color    = InkTheme.colors.textMuted,
                    )
                }
            }

            // Discount tag
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.accentPrimary)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text       = "40% OFF",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                )
            }
        }
    }
}

// ── Filter Genre Chips ────────────────────────────────────────────────────────

@Composable
private fun FilterChips(
    selected: StoryGenreFeed?,
    onSelect: (StoryGenreFeed?) -> Unit,
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier              = Modifier.padding(vertical = 10.dp),
    ) {
        val filterItems = listOf(
            null to "All Books",
            StoryGenreFeed.STORIES   to "Stories",
            StoryGenreFeed.ENTERTAINMENT   to "Entertainment",
            StoryGenreFeed.ARTICLES   to "Articles",
            StoryGenreFeed.DOCTOR     to "Doctor",
            StoryGenreFeed.MOVIES    to "Movies",
        )
        items(filterItems) { (genre, label) ->
            val isActive = genre == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isActive) Modifier.background(Brush.linearGradient(GradientAccent))
                        else Modifier.background(InkTheme.colors.bgSurface)
                            .border(1.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(20.dp))
                    )
                    .clickable { onSelect(genre) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text       = label,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isActive) Color.White else InkTheme.colors.textSecondary,
                )
            }
        }
    }
}
// ── Books Grid ────────────────────────────────────────────────────────────────

@Composable
fun BooksGrid(
    books: List<Book>,
    onBookClick: () -> Unit,
    onAddToCart: () -> Unit,
) {
    // Use a non-lazy grid inside a Column since parent is LazyColumn
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        books.chunked(2).forEachIndexed { rowIndex, rowBooks ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowBooks.forEach { book ->
                    AnimatedBookCard(
                        book        = book,
                        index       = rowIndex * 2 + rowBooks.indexOf(book),
                        onClick     = { onBookClick() },
                        onAddToCart = onAddToCart,
                        modifier    = Modifier.weight(1f),
                    )
                }
                // Fill last row gap if odd number
                if (rowBooks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun AnimatedBookCard(
    book: Book,
    index: Int,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(book.id) {
        delay(index * 80L)
        visible = true
    }
    AnimatedVisibility(
        visible  = visible,
        enter    = fadeIn(tween(350)) + scaleIn(tween(350), initialScale = 0.92f),
        modifier = modifier,
    ) {
        BookCard(book = book, onClick = onClick, onAddToCart = onAddToCart)
    }
}

@Composable
private fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
) {
    var wishlisted by remember { mutableStateOf(book.isWishlisted) }

    PressScaleButton(onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(InkTheme.colors.bgSurface)
                .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(16.dp)),
        ) {
            // Cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                        )
                    )
                    .background(Brush.linearGradient(book.coverGradient)),
            ) {
                // Spine
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(book.coverGradient.first().copy(alpha = 0.6f))
                )
                // Center circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(book.coverGradient.first().copy(alpha = 0.8f))
                        .border(1.5.dp, book.genre.color.copy(alpha = 0.5f), CircleShape),
                ) {
                    Text(book.coverEmoji, fontSize = 20.sp)
                }
                // Wishlist button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (wishlisted) InkTheme.colors.dangerRed.copy(alpha = 0.2f)
                            else Color.Black.copy(alpha = 0.4f)
                        )
                        .border(
                            1.dp,
                            if (wishlisted) InkTheme.colors.dangerRed.copy(alpha = 0.4f)
                            else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(9.dp),
                        )
                        .clickable { wishlisted = !wishlisted },
                ) {
                    val heartScale by animateFloatAsState(
                        targetValue = if (wishlisted) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "heartScale",
                    )
                    Icon(
                        imageVector = if (wishlisted) Icons.Rounded.Favorite
                        else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (wishlisted) InkTheme.colors.dangerRed else InkTheme.colors.textSecondary,
                        modifier = Modifier.size(13.dp).scale(heartScale),
                    )
                }
            }

            // Body
            Column(modifier = Modifier.padding(12.dp)) {
                GenreTag(label = book.genre.label, color = book.genre.color)
                Spacer(Modifier.height(3.dp))
                Text(
                    text = book.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkTheme.colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    letterSpacing = (-0.1).sp,
                )
                Text(
                    text = book.author,
                    fontSize = 11.sp,
                    color = InkTheme.colors.textMuted,
                )
                Spacer(Modifier.height(6.dp))
                StarRating(rating = book.rating, starSize = 11)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "$${book.price.format(2)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = InkTheme.colors.accentLight,
                            letterSpacing = (-0.3).sp,
                        )
                        book.discountPercent?.let {
                            Text(
                                text = "-$it%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkTheme.colors.successGreen,
                            )
                        }
                    }
                    // Add to cart mini button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Brush.linearGradient(GradientAccent))
                            .clickable(onClick = onAddToCart),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add to cart",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}
