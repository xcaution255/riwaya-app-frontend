package com.excaution.riwayaapp.presentation.auth.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.components.PressScaleButton
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgSurface
import com.excaution.riwayaapp.presentation.theme.GenreHorror
import com.excaution.riwayaapp.presentation.theme.GenreMystery
import com.excaution.riwayaapp.presentation.theme.GenreSciFi
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.GradientFeatured
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.StarColor
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import com.excaution.riwayaapp.presentation.theme.TextSecondary

import kotlinx.coroutines.launch

// ── Entry point ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope      = rememberCoroutineScope()

    val goNext: () -> Unit = {
        if (pagerState.currentPage < onboardingPages.lastIndex) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        } else {
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkTheme.colors.bgDeep),
    ) {
        // ── Pager ─────────────────────────────────────────────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { pageIndex ->
            // Calculate how far this page is from center (for parallax / fade)
            val pageOffset = (pagerState.currentPage - pageIndex) +
                pagerState.currentPageOffsetFraction

            OnboardingPage(
                page        = onboardingPages[pageIndex],
                pageOffset  = pageOffset,
                isActive    = pagerState.currentPage == pageIndex,
                onSkip      = onFinished,
                onNext      = goNext,
                pagerState  = pagerState,
            )
        }
    }
}

// ── Single Page ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPage(
    page: OnboardingPage,
    pageOffset: Float,
    isActive: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    pagerState: PagerState,
) {
    // Parallax helpers
    val contentAlpha by animateFloatAsState(
        targetValue   = if (isActive) 1f else 0f,
        animationSpec = tween(320),
        label         = "contentAlpha",
    )
    val contentTranslateX = pageOffset * 60f  // art parallax

    Box(modifier = Modifier.fillMaxSize()) {

        // Ambient background blobs
        AmbientBlobs(page = page, pageOffset = pageOffset)

        // Skip
        if (page.index < onboardingPages.lastIndex) {
            Text(
                text     = "Skip",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color    = InkTheme.colors.textFaint,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 52.dp, end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.bgSurface)
                    .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(8.dp))
                    .clickable(onClick = onSkip)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            //Art area
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer { translationX = contentTranslateX },
            ) {
                when (page.index) {
                    0 -> WriteArt(isActive = isActive, alpha = contentAlpha)
                    1 -> DiscoverArt(isActive = isActive, alpha = contentAlpha)
                    2 -> EarnArt(isActive = isActive, alpha = contentAlpha)
                }
            }

            // ── Text + Nav ────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = contentAlpha },
            ) {
                // Tag
                Text(
                    text     = page.tag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color    = page.accentColor,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 28.dp),
                )
                Spacer(Modifier.height(8.dp))

                // Title
                Text(
                    text       = page.title,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = InkTheme.colors.textPrimary,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.6).sp,
                    modifier   = Modifier.padding(horizontal = 28.dp),
                )
                Spacer(Modifier.height(10.dp))

                // Description
                Text(
                    text      = page.description,
                    fontSize  = 14.sp,
                    color     = InkTheme.colors.textSecondary,
                    lineHeight = 21.sp,
                    modifier  = Modifier.padding(horizontal = 28.dp),
                )
                Spacer(Modifier.height(28.dp))

                // Dots
                DotsIndicator(
                    count   = onboardingPages.size,
                    current = page.index,
                    activeColor = page.accentColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                )

                // CTA Button
                PressScaleButton(
                    onClick  = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(page.accentColor, page.accentColor.copy(alpha = 0.75f))
                                )
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),

                        ) {
                            Text(
                                text       = page.ctaLabel,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White,
                            )
                            if (page.index < onboardingPages.lastIndex) {
                                Text("→", fontSize = 18.sp, color = Color.White)
                            } else {
                                Text("✓", fontSize = 18.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Dots Indicator

@Composable
private fun DotsIndicator(
    count: Int,
    current: Int,
    activeColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier,
    ) {
        repeat(count) { i ->
            val isActive = i == current
            val width by animateDpAsState(
                targetValue   = if (isActive) 22.dp else 6.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label         = "dotWidth$i",
            )
            val color by animateColorAsState(
                targetValue   = if (isActive) activeColor else InkTheme.colors.bgBorder,
                animationSpec = tween(300),
                label         = "dotColor$i",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

// ── Ambient Blobs ─────────────────────────────────────────────────────────────

@Composable
private fun AmbientBlobs(page: OnboardingPage, pageOffset: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-right blob
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 160.dp + (pageOffset * 20).dp, y = (-60).dp)
                .clip(CircleShape)
                .alpha(0.05f)
                .background(page.accentColor),
        )
        // Bottom-left blob
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = (-40).dp, y = 480.dp)
                .clip(CircleShape)
                .alpha(0.04f)
                .background(page.accentColor),
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ── PAGE 1 ART – Write ────────────────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WriteArt(isActive: Boolean, alpha: Float) {
    // Cursor blink
    val cursorAlpha by rememberInfiniteTransition(label = "cursor").animateFloat(
        initialValue  = 1f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(tween(550, easing = StepEasing), RepeatMode.Reverse),
        label         = "cursorAlpha",
    )

    // Text lines appearing staggered
    val lineProgress by animateFloatAsState(
        targetValue   = if (isActive) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label         = "lineProgress",
    )

    // Float animation
    val floatY by rememberInfiniteTransition(label = "float").animateFloat(
        initialValue  = 0f,
        targetValue   = -8f,
        animationSpec = infiniteRepeatable(tween(2800, easing = EaseInOut), RepeatMode.Reverse),
        label         = "floatY",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
    ) {
        // Floating stat – reads
        FloatingStatCard(
            icon       = "👁",
            iconBg     = InkTheme.colors.successGreen.copy(alpha = 0.12f),
            value      = "142k",
            label      = "Reads",
            modifier   = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 32.dp)
                .offset(y = floatY.dp),
        )

        // Editor card
        Column(
            modifier = Modifier
                .width(240.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(18.dp))
                .padding(18.dp),
        ) {
            // Window dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp),
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (i) {
                                    0 -> InkTheme.colors.accentPrimary
                                    1 -> InkTheme.colors.accentLight
                                    else -> InkTheme.colors.bgBorder
                                }
                            ),
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "FANTASY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkTheme.colors.accentPrimary,
                    letterSpacing = 0.8.sp,
                )
            }

            // Title with blinking cursor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "The Last Starweaver",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = InkTheme.colors.textPrimary,
                )
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .width(2.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .alpha(cursorAlpha)
                        .background(InkTheme.colors.accentPrimary),
                )
            }

            Spacer(Modifier.height(12.dp))

            // Animated text lines
            val lineWidths = listOf(0.9f, 0.75f, 0.85f, 0.60f, 0.45f)
            lineWidths.forEachIndexed { index, maxWidth ->
                val lineDelay   = index * 0.12f
                val lineAlpha   = ((lineProgress - lineDelay) / (1f - lineDelay))
                    .coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .fillMaxWidth(maxWidth * lineAlpha)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .alpha((0.3f + lineAlpha * 0.5f).coerceIn(0f, 0.8f))
                        .background(InkTheme.colors.accentPrimary),
                )
            }

            Spacer(Modifier.height(12.dp))

            // Footer
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.fillMaxWidth(),
            ) {
                Text("Chapter 4 · 1,240 words", fontSize = 10.sp, color = TextFaint)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.horizontalGradient(listOf(InkTheme.colors.accentPrimary,
                            InkTheme.colors.accentLight))) //GradientAccent
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text("Save draft", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Floating stat – likes
        FloatingStatCard(
            icon     = "♥",
            iconBg   = InkTheme.colors.accentPrimary.copy(alpha = 0.12f),
            value    = "24.1k",
            label    = "Likes",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 20.dp)
                .offset(y = (-floatY).dp),
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ── PAGE 2 ART – Discover ────────────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun DiscoverArt(isActive: Boolean, alpha: Float) {
    val floatY by rememberInfiniteTransition(label = "disc-float").animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOut), RepeatMode.Reverse),
        label         = "discFloat",
    )

    // Stagger entrance for cards
    val cardEnter by animateFloatAsState(
        targetValue   = if (isActive) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label         = "cardEnter",
    )

    val genres = listOf(
        "🔮 Fantasy" to InkTheme.colors.accentPrimary,
        "💘 Romance" to InkTheme.colors.successGreen,
        "🔍 Mystery" to InkTheme.colors.genreMystery,
        "🚀 Sci-Fi"  to InkTheme.colors.genreSciFi,
        "😱 Horror"  to InkTheme.colors.genreHorror,
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.offset(y = floatY.dp),
        ) {
            // Genre pill strip (scrollable visual)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                genres.take(3).forEach { (label, color) ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(InkTheme.colors.bgCard)
                            .border(0.5.dp, color.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
                    }
                }
            }

            // Story card stack
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(220.dp),
            ) {
                // Card 3 (back)
                StoryStackCard(
                    title     = "The Gilded Cipher",
                    genre     = "MYSTERY",
                    genreColor = InkTheme.colors.genreMystery,
                    index     = 2,
                    enter     = cardEnter,
                    modifier  = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-34).dp)
                        .scale(0.86f)
                        .alpha(0.5f * cardEnter),
                )
                // Card 2 (middle)
                StoryStackCard(
                    title     = "Garden of Quiet Storms",
                    genre     = "ROMANCE",
                    genreColor = InkTheme.colors.successGreen,
                    index     = 1,
                    enter     = cardEnter,
                    modifier  = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-18).dp)
                        .scale(0.92f)
                        .alpha(0.75f * cardEnter),
                )
                // Card 1 (front)
                StoryCardFront(
                    enter    = cardEnter,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun StoryStackCard(
    title: String,
    genre: String,
    genreColor: Color,
    index: Int,
    enter: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InkTheme.colors.bgCard)
            .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp))
            .graphicsLayer { translationY = (1f - enter) * 30f * (index + 1) }
            .padding(14.dp),
    ) {
        Text(genre, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = genreColor, letterSpacing = 0.6.sp)
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkTheme.colors.textPrimary, letterSpacing = (-0.2).sp)
    }
}

@Composable
private fun StoryCardFront(enter: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(InkTheme.colors.bgSurface)
            .border(0.5.dp, InkTheme.colors.bgBorder.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .graphicsLayer { translationY = (1f - enter) * 20f }
            .padding(16.dp),
    ) {
        Text("FANTASY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkTheme.colors.accentPrimary, letterSpacing = 0.6.sp)
        Spacer(Modifier.height(4.dp))
        Text("The Last Starweaver", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.textPrimary, letterSpacing = (-0.3).sp)
        Text("by Elara Voss", fontSize = 11.sp, color = InkTheme.colors.textMuted, modifier = Modifier.padding(bottom = 10.dp))

        // Reading progress
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(InkTheme.colors.bgBorder),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Brush.horizontalGradient(
                        listOf(InkTheme.colors.accentPrimary
                    ,InkTheme.colors.accentLight)),
            ))
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("♥ 24.1k", fontSize = 11.sp, color = InkTheme.colors.textFaint)
            Text("💬 1.8k", fontSize = 11.sp, color = InkTheme.colors.textFaint)
            Text("⏱ 87 min", fontSize = 11.sp, color = InkTheme.colors.textFaint)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ── PAGE 3 ART – Earn ─────────────────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EarnArt(isActive: Boolean, alpha: Float) {
    val floatY1 by rememberInfiniteTransition(label = "earn-f1").animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse),
        label         = "ef1",
    )
    val floatY2 by rememberInfiniteTransition(label = "earn-f2").animateFloat(
        initialValue  = 0f,
        targetValue   = -12f,
        animationSpec = infiniteRepeatable(tween(3800, easing = EaseInOut, delayMillis = 400), RepeatMode.Reverse),
        label         = "ef2",
    )

    // Earnings count-up
    val earnProgress by animateFloatAsState(
        targetValue   = if (isActive) 1f else 0f,
        animationSpec = tween(1400, delayMillis = 300, easing = FastOutSlowInEasing),
        label         = "earnProgress",
    )
    val earnings = (earnProgress * 1240).toInt()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
    ) {
        // New sale pill (top right)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 20.dp)
                .offset(y = floatY1.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.successGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.successGreen.copy(alpha = 0.12f)),
            ) { Text("💰", fontSize = 14.sp) }
            Column {
                Text("+\$8.99", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.successGreen)
                Text("New sale", fontSize = 10.sp, color = InkTheme.colors.textFaint)
            }
        }

        // Main store card
        Column(
            modifier = Modifier
                .width(230.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(18.dp)),
        ) {
            // Book cover
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Brush.linearGradient(GradientFeatured)), //
            ) {
                // Book spine
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(10.dp)
                        .align(Alignment.CenterStart)
                        .background(Color.Black.copy(alpha = 0.3f)),
                )
                // Book cover art
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A1F5A))
                        .border(0.5.dp, Color(0xFF5A4A9A), RoundedCornerShape(10.dp)),
                ) {
                    Text("✦", fontSize = 28.sp)
                }
            }

            // Book info
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(InkTheme.colors.successGreen.copy(alpha = 0.12f))
                        .border(0.5.dp, InkTheme.colors.successGreen.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("🔥 BESTSELLER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkTheme.colors.successGreen)
                }
                Spacer(Modifier.height(8.dp))
                Text("Echoes of the Void", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.textPrimary, letterSpacing = (-0.2).sp)
                Text("by Elena Vasquez", fontSize = 11.sp, color = InkTheme.colors.textMuted, modifier = Modifier.padding(bottom = 8.dp))
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    repeat(4) { Text("★", fontSize = 12.sp, color = InkTheme.colors.starColor) }
                    Text("★", fontSize = 12.sp, color = InkTheme.colors.starColor.copy(alpha = 0.35f))
                    Text(" (4,821)", fontSize = 11.sp, color = InkTheme.colors.textMuted)
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        Text("\$8.99", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.accentLight, letterSpacing = (-0.4).sp)
                        Text("\$14.99", fontSize = 11.sp, color = InkTheme.colors.textFaint, textDecoration = TextDecoration.LineThrough)
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.horizontalGradient(GradientAccent))
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                    ) {
                        Text("Add to cart", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Monthly earnings pill (bottom left)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 16.dp)
                .offset(y = floatY2.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.accentPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.12f)),
            ) { Text("📈", fontSize = 14.sp) }
            Column {
                Text("\$$earnings", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.accentLight)
                Text("This month", fontSize = 10.sp, color = InkTheme.colors.textFaint)
            }
        }
    }
}

// ── Floating stat helper ──────────────────────────────────────────────────────

@Composable
private fun FloatingStatCard(
    icon: String,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(InkTheme.colors.bgCard)
            .border(0.5.dp, BgBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
        ) { Text(icon, fontSize = 13.sp) }
        Column {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.textPrimary)
            Text(label, fontSize = 10.sp, color = InkTheme.colors.textFaint)
        }
    }
}

// ── Step easing (for cursor blink) ───────────────────────────────────────────

private val StepEasing = Easing { fraction -> if (fraction < 0.5f) 0f else 1f }
private val EaseInOut  = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)