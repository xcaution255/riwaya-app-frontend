package com.excaution.riwayaapp.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.domain.model.StoryFeedItem
import com.excaution.riwayaapp.domain.model.StoryGenreFeed
import com.excaution.riwayaapp.format
import com.excaution.riwayaapp.presentation.theme.InkTheme


// ── Feed item composable ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryFeedCard(
    item: StoryFeedItem,
    modifier: Modifier = Modifier,
    onCommentsClick: (StoryFeedItem) -> Unit = {},
    onShareClick: (StoryFeedItem) -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }
    var liked      by remember { mutableStateOf(item.isLiked) }
    var saved      by remember { mutableStateOf(item.isSaved) }
    var likeCount  by remember { mutableLongStateOf(item.likeCount + if (item.isLiked) 1 else 0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(InkTheme.colors.bgCard)
            .clickable { isExpanded = !isExpanded }
            .border(BorderStroke(0.dp, Color.Transparent)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {

            // ── Meta row: genre pill + live badge + author ────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            ) {
                // Live badge
               // if (item.isLive) LiveBadge()

                // Genre pill
                GenrePill(genre = item.genre)

                // Video indicator
                //if (item.hasVideo) VideoBadge()

                Spacer(Modifier.weight(1f))

                // Author
                AuthorChip(
                    name       = item.authorName,
                    initial    = item.authorInitial,
                    gradient   = item.authorGradient,
                    isAuthor   = item.isAuthor,
                )
            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text       = item.title,
                fontSize   = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = InkTheme.colors.textPrimary,
                lineHeight = 21.sp,
                letterSpacing = (-0.3).sp,
                modifier   = Modifier.padding(bottom = 7.dp),
            )

            // ── Body (collapsed / expanded) ───────────────────────────────────
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(180))
                },
                label = "bodyExpand-${item.id}",
            ) { expanded ->
                if (expanded) {
                    // Full story body
                    Text(
                        text       = item.fullBody,
                        fontSize   = 13.sp,
                        color      = InkTheme.colors.textSecondary,
                        lineHeight = 21.sp,
                    )
                } else {
                    // Preview with fade + Read more
                    Column {
                        Box {
                            Text(
                                text       = item.previewBody,
                                fontSize   = 13.sp,
                                color      = InkTheme.colors.textSecondary,
                                lineHeight = 21.sp,
                                maxLines   = 3,
                                overflow   = TextOverflow.Ellipsis,
                            )
                            // Bottom fade scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, InkTheme.colors.bgCard)
                                        )
                                    ),
                            )
                        }
                        Text(
                            text       = "Read more →",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = InkTheme.colors.accentLight,
                            modifier   = Modifier
                                .padding(top = 4.dp)
                                .clickable { isExpanded = true },
                        )
                    }
                }
            }

            // ── Chapter info row ──────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .border(color = InkTheme.colors.bgBorder, width = 0.5.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .padding(top = 10.dp),
            ) {
                MetaChip(Icons.Rounded.Visibility, item.views)
                MetaChip(Icons.Rounded.Schedule, "${item.readTimeMin} min")
                MetaChip(Icons.Rounded.AutoStories, item.chapterLabel)
                Spacer(Modifier.weight(1f))
                if (item.timeAgo != null) {
                    Text(item.timeAgo, fontSize = 11.sp, color = InkTheme.colors.textFaint)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(Icons.Rounded.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                        Text(
                            text = item.rating.toDouble().format(decimals = 1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }

        // ── Divider ───────────────────────────────────────────────────────────
        HorizontalDivider(color = InkTheme.colors.bgBorder, thickness = 0.5.dp)

        // ── Action bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Like
            ActionButton(
                icon       = if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                label      = formatCount(likeCount),
                tint       = if (liked) Color(0xFFE24B4A) else InkTheme.colors.textMuted,
                modifier   = Modifier.weight(1f),
                onClick    = {
                    liked = !liked
                    likeCount = item.likeCount + if (liked) 1 else 0
                },
            )
            ActionSeparator()
            // Comments
            ActionButton(
                icon     = Icons.Rounded.ChatBubbleOutline,
                label    = item.commentCount,
                modifier = Modifier.weight(1f),
                onClick  = { onCommentsClick(item) },
            )
            ActionSeparator()
            // Save
            ActionButton(
                icon     = if (saved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                label    = if (saved) "Saved" else "Save",
                tint     = if (saved) InkTheme.colors.accentPrimary else InkTheme.colors.textMuted,
                modifier = Modifier.weight(1f),
                onClick  = { saved = !saved },
            )
            ActionSeparator()
            // Share
            ActionButton(
                icon     = Icons.Rounded.Share,
                label    = "Share",
                modifier = Modifier.weight(1f),
                onClick  = { onShareClick(item) },
            )
        }

        // Divider below actions
        HorizontalDivider(color = InkTheme.colors.bgBorder, thickness = 0.5.dp)
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

@Composable
private fun LiveBadge() {
    val pulse by rememberInfiniteTransition(label = "livePulse").animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOut), RepeatMode.Reverse),
        label         = "liveDot",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFE24B4A).copy(alpha = 0.1f))
            .border(0.5.dp, Color(0xFFE24B4A).copy(alpha = 0.25f), CircleShape)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFE24B4A).copy(alpha = pulse)),
        )
        Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE24B4A), letterSpacing = 0.5.sp)
    }
}

@Composable
private fun GenrePill(genre: StoryGenreFeed) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(genre.color.copy(alpha = genre.bgAlpha))
            .border(0.5.dp, genre.color.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text       = genre.label.uppercase(),
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = genre.color,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun VideoBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Rounded.PlayArrow, null, tint = Color(0xFFEF9F27), modifier = Modifier.size(13.dp))
        Text("VIDEO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF9F27), letterSpacing = 0.5.sp)
    }
}

@Composable
private fun AuthorChip(
    name: String,
    initial: String,
    gradient: List<Color>,
    isAuthor: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient)),
        ) {
            Text(initial, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Text(name, fontSize = 11.sp, color = InkTheme.colors.textMuted, fontWeight = FontWeight.Medium)
        if (isAuthor) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.12f))
                    .border(0.5.dp, InkTheme.colors.accentPrimary.copy(alpha = 0.25f), CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text("AUTHOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = InkTheme.colors.accentLight, letterSpacing = 0.4.sp)
            }
        }
    }
}

@Composable
private fun MetaChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, tint = InkTheme.colors.textFaint, modifier = Modifier.size(13.dp))
        Text(label, fontSize = 11.sp, color = InkTheme.colors.textFaint)
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    tint: Color = InkTheme.colors.textMuted,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "actionScale",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clickable(onClick = onClick),
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = tint)
    }
}

@Composable
private fun ActionSeparator() {
    Box(
        modifier = Modifier
            .width(0.5.dp)
            .height(22.dp)
            .background(InkTheme.colors.bgBorder),
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatCount(n: Long): String = when {
    // Cast the math result to a Double first, then call your extension function
    n >= 1_000_000 -> (n / 1_000_000.0).format(decimals = 1) + "M"
    n >= 1_000     -> (n / 1_000.0).format(decimals = 1) + "k"
    else           -> n.toString()
}


private val EaseInOut = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
