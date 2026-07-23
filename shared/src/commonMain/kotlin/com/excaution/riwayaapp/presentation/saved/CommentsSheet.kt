package com.excaution.riwayaapp.presentation.saved

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.format
import com.excaution.riwayaapp.presentation.components.InitialsAvatar
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.GradientHorror
import com.excaution.riwayaapp.presentation.theme.GradientMystery
import com.excaution.riwayaapp.presentation.theme.GradientRomance
import com.excaution.riwayaapp.presentation.theme.GradientSciFi
import com.excaution.riwayaapp.presentation.theme.InkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock


// ── Data models

data class Comment(
    val id: Int,
    val authorName: String,
    val authorInitial: String,
    val authorGradient: List<Color>,
    val isAuthor: Boolean = false,
    val body: String,
    val timeLabel: String,
    val likes: Int,
    val isLiked: Boolean = false,
    val reactions: Map<String, Int> = emptyMap(),
    val replies: List<Comment> = emptyList(),
    val isPinned: Boolean = false,
)

private val emojiReactions = listOf("❤️", "🔥", "😮", "😭", "👏", "✦", "🤯")

private val sampleComments = listOf(
    Comment(
        id = 1, authorName = "Elara Voss", authorInitial = "E",
        authorGradient = GradientAccent, isAuthor = true, isPinned = true,
        body = "Chapter 12 drops Friday 🔥 thank you for every single comment — you all keep me writing!",
        timeLabel = "2m", likes = 482, isLiked = true,
        reactions = mapOf("❤️" to 482, "🔥" to 91),
    ),
    Comment(
        id = 2, authorName = "Mira Chen", authorInitial = "M",
        authorGradient = GradientRomance,
        body = "The twist in chapter 11 destroyed me 😭 I was NOT prepared for that ending",
        timeLabel = "14m", likes = 241,
        reactions = mapOf("😭" to 38),
        replies = listOf(
            Comment(
                id = 21, authorName = "Ade Okonkwo", authorInitial = "A",
                authorGradient = GradientMystery,
                body = "@Mira same! I literally threw my phone 💀",
                timeLabel = "8m", likes = 12,
            ),
            Comment(
                id = 22, authorName = "Sol Martinez", authorInitial = "S",
                authorGradient = GradientSciFi,
                body = "Chapter 11 broke my whole week honestly 😭",
                timeLabel = "5m", likes = 8,
            ),
        ),
    ),
    Comment(
        id = 3, authorName = "Sol Martinez", authorInitial = "S",
        authorGradient = GradientSciFi,
        body = "@Elara Voss the world-building in this chapter is unreal. How long does each chapter take you?",
        timeLabel = "32m", likes = 78,
        reactions = mapOf("🤔" to 14),
    ),
    Comment(
        id = 4, authorName = "K. Darkmore", authorInitial = "K",
        authorGradient = GradientHorror,
        body = "Been following since chapter 1 and this series just keeps getting better. Absolute masterpiece 👏",
        timeLabel = "1h", likes = 156, isLiked = true,
    ),
    Comment(
        id = 5, authorName = "Jane Amara", authorInitial = "J",
        authorGradient = listOf(Color(0xFF1a1a0a), Color(0xFF3a3a10)),
        body = "I haven't cried at a book in years. Chapter 11 broke that streak spectacularly 😭✦",
        timeLabel = "2h", likes = 94,
    ),
)

// ── Sheet trigger ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    totalComments: Int,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var show by remember { mutableStateOf(true) }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = { show = false; onDismiss() },
            sheetState = sheetState,
            containerColor = InkTheme.colors.bgDeep,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(36.dp).height(4.dp)
                        .clip(CircleShape).background(InkTheme.colors.bgBorder),
                )
            },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            CommentsContent(
                totalComments = totalComments,
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    show = false; onDismiss()
                },
            )
        }
    }
}

// Sheet content
@Composable
fun CommentsContent(
    totalComments: Int = 4821,
    onDismiss: () -> Unit = {},
) {
    var comments by remember { mutableStateOf(sampleComments) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope     = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
    ) {
        // ── Pinned banner ─────────────────────────────────────────────────────
        val pinned = comments.firstOrNull { it.isPinned }
        if (pinned != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.07f))
                    .border(
                        BorderStroke(0.dp, Color.Transparent),
                        RoundedCornerShape(0.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(InkTheme.colors.accentPrimary),
                )
                Icon(Icons.Rounded.PushPin, null, tint = InkTheme.colors.accentPrimary, modifier = Modifier.size(12.dp))
                Text("Pinned by author", style = InkTheme.typography.labelSmall, color = InkTheme.colors.accentPrimary)
                Text("· \"${pinned.body.take(32)}…\"", fontSize = 11.sp, color = InkTheme.colors.textMuted, maxLines = 1)
            }
        }

        // ── Sheet header ──────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(0.5.dp, InkTheme.colors.bgBorder.copy(alpha = 0.5f)), RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Column {
                Text("Comments", style = InkTheme.typography.titleLarge, color = InkTheme.colors.textPrimary, letterSpacing = (-0.3).sp)
                Text("${totalComments.toDouble().format(1)} comments", style = InkTheme.typography.bodySmall, color = InkTheme.colors.textMuted)
            }
            // Sort button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.bgCard)
                    .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(8.dp))
                    .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Icon(Icons.Rounded.SwapVert, null, tint = InkTheme.colors.textSecondary, modifier = Modifier.size(13.dp))
                Text("Top", style = InkTheme.typography.bodySmall, color = InkTheme.colors.textSecondary)
            }
        }

        // ── Emoji quick-react row ─────────────────────────────────────────────
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 6.dp),
        ) {
            items(emojiReactions, key = { it }) { emoji ->
                EmojiButton(emoji = emoji)
            }
        }

        HorizontalDivider(color = InkTheme.colors.bgBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

        // ── Comments list ─────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(
                items       = comments,
                key         = { _, c -> c.id },
                contentType = { _, _ -> "comment" },
            ) { index, comment ->
                CommentItem(
                    comment  = comment,
                    index    = index,
                    onLike   = { id ->
                        comments = comments.map {
                            if (it.id == id) it.copy(
                                isLiked = !it.isLiked,
                                likes   = if (!it.isLiked) it.likes + 1 else it.likes - 1,
                            ) else it
                        }
                    },
                    onReply  = { /* focus input */ },
                )
            }
        }

        // ── Input bar ─────────────────────────────────────────────────────────
        HorizontalDivider(color = InkTheme.colors.bgBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(InkTheme.colors.bgDeep)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding(),
        ) {
            InitialsAvatar(initial = "A", size = 28.dp, fontSize = 10)
            BasicTextField(
                value         = inputText,
                onValueChange = { inputText = it },
                singleLine    = true,
                textStyle = TextStyle(color = InkTheme.colors.textPrimary, fontSize = 13.sp),
                cursorBrush = SolidColor(InkTheme.colors.accentPrimary),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(InkTheme.colors.bgSurface)
                            .border(0.5.dp, InkTheme.colors.bgBorder, CircleShape)
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                    ) {
                        if (inputText.isEmpty()) {
                            Text("Add a comment…", style = InkTheme.typography.bodyMedium, color = InkTheme.colors.textFaint)
                        }
                        inner()
                    }
                },
                modifier = Modifier.weight(1f),
            )
            Text("😊", fontSize = 20.sp, modifier = Modifier.clickable { })
            // Send
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(GradientAccent))
                    .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {
                        if (inputText.isNotBlank()) {
                            comments = listOf(
                                Comment(
                                    id = Clock.System.now().toEpochMilliseconds().toInt(),
                                    authorName = "Augustino", authorInitial = "A",
                                    authorGradient = GradientAccent,
                                    body = inputText, timeLabel = "now", likes = 0,
                                )
                            ) + comments
                            inputText = ""
                            scope.launch { listState.animateScrollToItem(0) }
                        }
                    },
            ) {
                Icon(Icons.Rounded.Send, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Single comment ────────────────────────────────────────────────────────────

@Composable
private fun CommentItem(
    comment: Comment,
    index: Int,
    onLike: (Int) -> Unit,
    onReply: (Int) -> Unit,
) {
    var repliesExpanded by remember { mutableStateOf(false) }

    // Staggered entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(comment.id) {
        delay(index * 55L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -14 },
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                // Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(comment.authorGradient)),
                ) {
                    Text(comment.authorInitial, style = InkTheme.typography.titleMedium, color = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    // Header row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(comment.authorName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkTheme.colors.textPrimary)
                        if (comment.isAuthor) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.12f))
                                    .border(0.5.dp, InkTheme.colors.accentPrimary.copy(alpha = 0.25f), CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 2.dp),
                            ) {
                                Text("AUTHOR", style = InkTheme.typography.labelSmall, color = InkTheme.colors.accentLight, letterSpacing = 0.4.sp)
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text(comment.timeLabel, style = InkTheme.typography.labelSmall, color = InkTheme.colors.textFaint)
                    }

                    Spacer(Modifier.height(3.dp))

                    // Body
                    Text(
                        text       = comment.body,
                        style = InkTheme.typography.titleMedium,
                        color      = InkTheme.colors.textSecondary
                    )

                    // Reaction pills
                    if (comment.reactions.isNotEmpty()) {
                        Spacer(Modifier.height(5.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            comment.reactions.forEach { (emoji, count) ->
                                ReactionPill(emoji = emoji, count = count)
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Actions row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        LikeButton(
                            count   = comment.likes,
                            isLiked = comment.isLiked,
                            onClick = { onLike(comment.id) },
                        )
                        Text(
                            text     = if (comment.replies.isNotEmpty()) "Reply · ${comment.replies.size}" else "Reply",
                            style = InkTheme.typography.bodySmall,
                            color    = if (repliesExpanded) InkTheme.colors.accentPrimary else InkTheme.colors.textFaint,
                            modifier = Modifier.clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {
                                if (comment.replies.isNotEmpty()) repliesExpanded = !repliesExpanded
                                else onReply(comment.id)
                            },
                        )
                        Spacer(Modifier.weight(1f))
                        Text("···", fontSize = 13.sp, color = InkTheme.colors.textFaint, modifier = Modifier.clickable { })
                    }
                }
            }

            // Nested replies
            AnimatedVisibility(
                visible = repliesExpanded,
                enter   = fadeIn(tween(250)) + expandVertically(tween(250)),
                exit    = fadeOut(tween(200)) + shrinkVertically(tween(200)),
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 54.dp, end = 14.dp)
                        .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp)
                        )
                        .padding(start = 10.dp),
                ) {
                    comment.replies.forEachIndexed { i, reply ->
                        ReplyItem(reply = reply, index = i)
                    }
                }
            }

            // Subtle divider
            HorizontalDivider(
                color     = InkTheme.colors.bgBorder.copy(alpha = 0.3f),
                thickness = 0.5.dp,
                modifier  = Modifier.padding(start = 54.dp, end = 14.dp),
            )
        }
    }
}

@Composable
private fun ReplyItem(reply: Comment, index: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(reply.authorGradient)),
        ) {
            Text(reply.authorInitial, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(reply.authorName, style = InkTheme.typography.labelSmall, color = InkTheme.colors.textPrimary)
                Text(reply.timeLabel, style = InkTheme.typography.labelSmall, color = InkTheme.colors.textFaint)
            }
            Text(reply.body, style = InkTheme.typography.bodySmall, color = InkTheme.colors.textSecondary, lineHeight = 18.sp)
        }
    }
}

// ── Reaction pill ─────────────────────────────────────────────────────────────

@Composable
private fun ReactionPill(emoji: String, count: Int) {
    var picked by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        if (picked) InkTheme.colors.accentPrimary.copy(alpha = 0.12f) else InkTheme.colors.bgCard, tween(180), "reactBg"
    )
    val borderColor by animateColorAsState(
        if (picked) InkTheme.colors.accentPrimary.copy(alpha = 0.3f) else InkTheme.colors.bgBorder, tween(180), "reactBdr"
    )
    val scale by animateFloatAsState(
        if (picked) 1.1f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "reactScale"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(bgColor)
            .border(0.5.dp, borderColor, CircleShape)
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { picked = !picked }
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(emoji, fontSize = 12.sp)
        Text(
            text = count.toString(),
            style = InkTheme.typography.bodySmall,
            color = if (picked) InkTheme.colors.accentLight else InkTheme.colors.textMuted,
            fontWeight = if (picked) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

// Like button with heart animation

@Composable
private fun LikeButton(count: Int, isLiked: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue   = if (isLiked) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "likeScale",
        finishedListener = { },
    )
    val color by animateColorAsState(
        if (isLiked) InkTheme.colors.dangerRed else InkTheme.colors.textFaint, tween(200), "likeColor"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {onClick()},
    ) {
        Text("♥", fontSize = 14.sp, color = color, modifier = Modifier.scale(scale))
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
            },
            label = "likeCount",
        ) { c ->
            Text(
                text = if (c >= 1000) "${c / 1000}.${(c % 1000) / 100}k" else c.toString(),
                style = InkTheme.typography.bodySmall,
                color    = color,
                fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

// ── Emoji button ──────────────────────────────────────────────────────────────

@Composable //here i will use launched effect in the future
private fun EmojiButton(emoji: String) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) 1.5f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "emojiScale"
    )
    Text(
        text     = emoji,
        fontSize = 22.sp,
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {
                pressed = true
                CoroutineScope(Dispatchers.Main).launch {
                    delay(250)
                    pressed = false
                }
            },
    )
}