package com.excaution.riwayaapp.presentation.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgSurface
import com.excaution.riwayaapp.presentation.theme.DangerRed
import com.excaution.riwayaapp.presentation.theme.GenreSciFi
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.GradientFantasy
import com.excaution.riwayaapp.presentation.theme.GradientHorror
import com.excaution.riwayaapp.presentation.theme.GradientRomance
import com.excaution.riwayaapp.presentation.theme.GradientSciFi
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.StarColor
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import com.excaution.riwayaapp.presentation.theme.TextSecondary
import kotlinx.coroutines.delay

// ── Models ────────────────────────────────────────────────────────────────────

enum class NotifFilter(val label: String) {
    ALL       ("All"),
    MENTIONS  ("Mentions"),
    COMMENTS  ("Comments"),
    LIKES     ("Likes"),
    FOLLOWS   ("Follows"),
    SALES     ("Sales"),
}

enum class NotifType {
    LIKE, COMMENT, FOLLOW, SALE, FEATURE, MILESTONE, NEW_CHAPTER, PAYOUT
}

data class Notification(
    val id: Int,
    val type: NotifType,
    val actorName: String?,         // null = system notification
    val actorInitials: String?,
    val actorGradient: List<Color>?,
    val title: String,
    val bodyHighlights: List<String> = emptyList(), // words to bold
    val body: String,
    val timeLabel: String,
    val isUnread: Boolean = false,
    val coverEmoji: String? = null,
    val coverGradient: List<Color>? = null,
    val badgeLabel: String? = null,
    val badgeColor: Color? = null,
    val actionLabel: String? = null,
    val earningsLabel: String? = null,
)

// ── Sample data ───────────────────────────────────────────────────────────────

private val sampleNotifications = listOf(
    Notification(
        id = 1, type = NotifType.LIKE,
        actorName = "Mira Chen", actorInitials = "MC",
        actorGradient = GradientRomance,
        title = "", body = "liked your story The Last Starweaver",
        bodyHighlights = listOf("Mira Chen", "41 others", "The Last Starweaver"),
        timeLabel = "2 minutes ago", isUnread = true,
        coverEmoji = "✦", coverGradient = GradientFantasy,
    ),
    Notification(
        id = 2, type = NotifType.COMMENT,
        actorName = "Sol Okafor", actorInitials = "SO",
        actorGradient = GradientFantasy,
        title = "", body = "commented: \"This chapter left me breathless! When's the next update??\"",
        bodyHighlights = listOf("Sol Okafor"),
        timeLabel = "18 minutes ago", isUnread = true,
        coverEmoji = "✦", coverGradient = GradientFantasy,
    ),
    Notification(
        id = 3, type = NotifType.FOLLOW,
        actorName = "K. Darkmore", actorInitials = "KD",
        actorGradient = GradientHorror,
        title = "", body = "started following you",
        bodyHighlights = listOf("K. Darkmore"),
        timeLabel = "1 hour ago", isUnread = true,
        actionLabel = "Follow",
    ),
    Notification(
        id = 4, type = NotifType.SALE,
        actorName = "Elena Vasquez", actorInitials = "EV",
        actorGradient = GradientSciFi,
        title = "Sale!", body = "purchased Echoes of the Void",
        bodyHighlights = listOf("Sale!", "Elena Vasquez", "Echoes of the Void"),
        timeLabel = "2 hours ago", isUnread = true,
        earningsLabel = "+\$8.99",
    ),
    Notification(
        id = 5, type = NotifType.FEATURE,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "", body = "The Last Starweaver was featured in the Editor's Picks collection",
        bodyHighlights = listOf("The Last Starweaver", "Editor's Picks"),
        timeLabel = "Yesterday, 4:30 PM",
        badgeLabel = "Featured", badgeColor = StarColor,
    ),
    Notification(
        id = 6, type = NotifType.COMMENT,
        actorName = "Jane Amara", actorInitials = "JA",
        actorGradient = GradientRomance,
        title = "", body = "replied to your comment on Garden of Quiet Storms",
        bodyHighlights = listOf("Jane Amara", "Garden of Quiet Storms"),
        timeLabel = "Yesterday, 11:12 AM",
    ),
    Notification(
        id = 7, type = NotifType.MILESTONE,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "Milestone!", body = "Your profile reached 4,000 followers",
        bodyHighlights = listOf("Milestone!", "4,000 followers"),
        timeLabel = "Yesterday, 9:00 AM",
    ),
    Notification(
        id = 8, type = NotifType.NEW_CHAPTER,
        actorName = "Sol Martinez", actorInitials = "SM",
        actorGradient = GradientSciFi,
        title = "", body = "published a new chapter in Echoes Beyond Orion you're following",
        bodyHighlights = listOf("Sol Martinez", "Echoes Beyond Orion"),
        timeLabel = "Mon, 3:22 PM",
        coverEmoji = "🚀", coverGradient = GradientSciFi,
    ),
    Notification(
        id = 9, type = NotifType.PAYOUT,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "Payout sent!", body = "\$1,240 has been transferred to your account",
        bodyHighlights = listOf("Payout sent!"),
        timeLabel = "Sun",
        earningsLabel = "+\$1,240.00",
    ),
    Notification(
        id = 10, type = NotifType.LIKE,
        actorName = "Mira Chen", actorInitials = "MC",
        actorGradient = GradientRomance,
        title = "", body = "liked your story The Last Starweaver",
        bodyHighlights = listOf("Mira Chen", "41 others", "The Last Starweaver"),
        timeLabel = "2 minutes ago", isUnread = true,
        coverEmoji = "✦", coverGradient = GradientFantasy,
    ),
    Notification(
        id = 11, type = NotifType.COMMENT,
        actorName = "Sol Okafor", actorInitials = "SO",
        actorGradient = GradientFantasy,
        title = "", body = "commented: \"This chapter left me breathless! When's the next update??\"",
        bodyHighlights = listOf("Sol Okafor"),
        timeLabel = "18 minutes ago", isUnread = true,
        coverEmoji = "✦", coverGradient = GradientFantasy,
    ),
    Notification(
        id = 12, type = NotifType.FOLLOW,
        actorName = "K. Darkmore", actorInitials = "KD",
        actorGradient = GradientHorror,
        title = "", body = "started following you",
        bodyHighlights = listOf("K. Darkmore"),
        timeLabel = "1 hour ago", isUnread = true,
        actionLabel = "Follow",
    ),
    Notification(
        id = 13, type = NotifType.SALE,
        actorName = "Elena Vasquez", actorInitials = "EV",
        actorGradient = GradientSciFi,
        title = "Sale!", body = "purchased Echoes of the Void",
        bodyHighlights = listOf("Sale!", "Elena Vasquez", "Echoes of the Void"),
        timeLabel = "2 hours ago", isUnread = true,
        earningsLabel = "+\$8.99",
    ),
    Notification(
        id = 14, type = NotifType.FEATURE,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "", body = "The Last Starweaver was featured in the Editor's Picks collection",
        bodyHighlights = listOf("The Last Starweaver", "Editor's Picks"),
        timeLabel = "Yesterday, 4:30 PM",
        badgeLabel = "Featured", badgeColor = StarColor,
    ),
    Notification(
        id = 15, type = NotifType.COMMENT,
        actorName = "Jane Amara", actorInitials = "JA",
        actorGradient = GradientRomance,
        title = "", body = "replied to your comment on Garden of Quiet Storms",
        bodyHighlights = listOf("Jane Amara", "Garden of Quiet Storms"),
        timeLabel = "Yesterday, 11:12 AM",
    ),
    Notification(
        id = 16, type = NotifType.MILESTONE,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "Milestone!", body = "Your profile reached 4,000 followers",
        bodyHighlights = listOf("Milestone!", "4,000 followers"),
        timeLabel = "Yesterday, 9:00 AM",
    ),
    Notification(
        id = 17, type = NotifType.NEW_CHAPTER,
        actorName = "Sol Martinez", actorInitials = "SM",
        actorGradient = GradientSciFi,
        title = "", body = "published a new chapter in Echoes Beyond Orion you're following",
        bodyHighlights = listOf("Sol Martinez", "Echoes Beyond Orion"),
        timeLabel = "Mon, 3:22 PM",
        coverEmoji = "🚀", coverGradient = GradientSciFi,
    ),
    Notification(
        id = 19, type = NotifType.PAYOUT,
        actorName = null, actorInitials = null, actorGradient = null,
        title = "Payout sent!", body = "\$1,240 has been transferred to your account",
        bodyHighlights = listOf("Payout sent!"),
        timeLabel = "Sun",
        earningsLabel = "+\$1,240.00",
    ),
)

private val sectionHeaders = mapOf(
    1 to "Today",
    5 to "Yesterday",
    8 to "This Week",
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun NotificationScreen(
    onBack: () -> Unit = {},
) {
    var selectedFilter by remember { mutableStateOf(NotifFilter.ALL) }
    var notifications  by remember { mutableStateOf(sampleNotifications) }

    val filtered = remember(selectedFilter, notifications) {
        when (selectedFilter) {
            NotifFilter.ALL      -> notifications
            NotifFilter.LIKES    -> notifications.filter { it.type == NotifType.LIKE }
            NotifFilter.COMMENTS -> notifications.filter { it.type == NotifType.COMMENT }
            NotifFilter.FOLLOWS  -> notifications.filter { it.type == NotifType.FOLLOW }
            NotifFilter.SALES    -> notifications.filter { it.type == NotifType.SALE || it.type == NotifType.PAYOUT }
            NotifFilter.MENTIONS -> notifications.filter { it.body.contains("mentioned") }
        }
    }

    // Stable list state — critical for smooth scroll
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = InkTheme.colors.bgDeep,
        topBar = {
            NotifTopBar(
                onBack       = onBack,
                onMarkAllRead = {
                    notifications = notifications.map { it.copy(isUnread = false) }
                },
                unreadCount = notifications.count { it.isUnread },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(InkTheme.colors.bgDeep)
                .padding(top = padding.calculateTopPadding()),
        ) {
            // Filter chips — outside LazyColumn so they don't scroll (sticky)
            FilterRow(selected = selectedFilter, onSelect = { selectedFilter = it })

            if (filtered.isEmpty()) {
                EmptyState(filter = selectedFilter)
            } else {
                // ── The LazyColumn for smooth scroll ──────────────────────────
                // Key optimizations:
                // 1. Use stable `key` on every item
                // 2. Use `contentType` so Compose reuses views of same type
                // 3. Date headers as separate items with their own keys
                // 4. `rememberLazyListState` passed in for external control
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 24.dp),
                    // This makes fling deceleration feel natural
                    flingBehavior = ScrollableDefaults.flingBehavior(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(InkTheme.colors.bgDeep),
                ) {
                    // Build items with section headers interleaved
                    filtered.forEachIndexed { index, notif ->
                        // Section header if applicable
                        sectionHeaders[notif.id]?.let { headerTitle ->
                            item(
                                key         = "header-$headerTitle",
                                contentType = "header",
                            ) {
                                DateHeader(title = headerTitle)
                            }
                        }
                        // Notification item
                        item(
                            key         = notif.id,
                            contentType = notif.type.name,  // reuse composable slots by type
                        ) {
                            AnimatedNotifItem(
                                notification = notif,
                                index        = index,
                                onDismiss    = {
                                    notifications = notifications.filter { it.id != notif.id }
                                },
                                onRead = {
                                    notifications = notifications.map {
                                        if (it.id == notif.id) it.copy(isUnread = false) else it
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun NotifTopBar(
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    unreadCount: Int,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(InkTheme.colors.bgDeep)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        // Back
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(InkTheme.colors.bgSurface)
                .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(10.dp))
                .clickable(onClick = onBack),
        ) {
            Icon(Icons.Rounded.ArrowBackIos, "Back", tint = InkTheme.colors.textSecondary, modifier = Modifier.size(15.dp).offset(x = 2.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Notifications", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.textPrimary, letterSpacing = (-0.4).sp)
            if (unreadCount > 0) {
                AnimatedContent(
                    targetState = unreadCount,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                    },
                    label = "unreadBadge",
                ) { count ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(18.dp)
                            .widthIn(min = 18.dp)
                            .clip(CircleShape)
                            .background(InkTheme.colors.accentPrimary)
                            .padding(horizontal = 5.dp),
                    ) {
                        Text(count.toString(), style = InkTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }
        }

        // Mark all read
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.bgSurface)
                    .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(8.dp))
                    .clickable(onClick = onMarkAllRead)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("Mark all read", style = InkTheme.typography.bodySmall, color = InkTheme.colors.textSecondary)
            }
        } else {
            Spacer(Modifier.width(80.dp))
        }
    }
}

// ── Filter Row ────────────────────────────────────────────────────────────────

@Composable
private fun FilterRow(
    selected: NotifFilter,
    onSelect: (NotifFilter) -> Unit,
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 4.dp),
        // This inner LazyRow must NOT conflict with outer LazyColumn scroll.
        // They scroll on different axes so there's no conflict.
    ) {
        items(
            items = NotifFilter.values().toList(),
            key   = { it.name },
        ) { filter ->
            val isActive = filter == selected
            val scale by animateFloatAsState(
                targetValue   = if (isActive) 1f else 0.96f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label         = "filterScale-${filter.name}",
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isActive) Modifier.background(Brush.linearGradient(GradientAccent))
                        else Modifier.background(InkTheme.colors.bgSurface).border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(16.dp))
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text       = filter.label,
                    style = InkTheme.typography.bodySmall,
                    color      = if (isActive) Color.White else InkTheme.colors.textSecondary,
                )
            }
        }
    }
}

// ── Date Section Header ───────────────────────────────────────────────────────

@Composable
private fun DateHeader(title: String) {
    Text(
        text          = title.uppercase(),
        style = InkTheme.typography.labelSmall,
        color         = InkTheme.colors.textFaint,
        modifier      = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    )
}

// ── Notification Item ─────────────────────────────────────────────────────────

@Composable
private fun AnimatedNotifItem(
    notification: Notification,
    index: Int,
    onDismiss: () -> Unit,
    onRead: () -> Unit,
) {
    // Staggered entrance — but only on initial composition, not on recomposition
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(notification.id) {
        delay(index * 40L) // tighter delay = snappier feel
        visible = true
    }

    // Swipe-to-dismiss support
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss(); true
            } else false
        },
        positionalThreshold = { it * 0.4f },
    )

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -16 },
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                // Red dismiss background revealed when swiping left
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(InkTheme.colors.dangerRed.copy(alpha = 0.12f))
                        .padding(end = 24.dp),
                ) {
                    Icon(Icons.Rounded.Delete, "Dismiss", tint = InkTheme.colors.dangerRed, modifier = Modifier.size(22.dp))
                }
            },
            enableDismissFromStartToEnd = false,
        ) {
            NotifItemContent(
                notification = notification,
                onRead       = onRead,
            )
        }
    }
}

@Composable
private fun NotifItemContent(
    notification: Notification,
    onRead: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRead)
            .background(
                if (notification.isUnread) InkTheme.colors.accentPrimary.copy(alpha = 0.04f)
                else InkTheme.colors.bgDeep
            ),
    ) {
        // Unread dot
        if (notification.isUnread) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 6.dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentPrimary),
            )
        }

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Actor avatar
            NotifAvatar(notification = notification)

            // Body
            Column(modifier = Modifier.weight(1f)) {
                // Annotated body text with bold highlights
                Text(
                    text = buildAnnotatedString {
                        if (notification.title.isNotEmpty()) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = InkTheme.colors.textPrimary)) {
                                append(notification.title)
                                append(" ")
                            }
                        }
                        if (notification.actorName != null) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = InkTheme.colors.textPrimary)) {
                                append(notification.actorName)
                            }
                            append(" ")
                        }
                        // Parse body for highlights
                        var remaining = notification.body
                        notification.bodyHighlights
                            .filter { it != notification.title && it != notification.actorName }
                            .forEach { highlight ->
                                val idx = remaining.indexOf(highlight)
                                if (idx >= 0) {
                                    append(remaining.substring(0, idx))
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = InkTheme.colors.textPrimary)) {
                                        append(highlight)
                                    }
                                    remaining = remaining.substring(idx + highlight.length)
                                }
                            }
                        append(remaining)
                    },
                    style = InkTheme.typography.bodyMedium,
                    color      = InkTheme.colors.textSecondary
                )

                Spacer(Modifier.height(3.dp))
                Text(
                    text     = notification.earningsLabel?.let { "$it · ${notification.timeLabel}" } ?: notification.timeLabel,
                    style = InkTheme.typography.bodySmall,
                    color    = if (notification.earningsLabel != null) InkTheme.colors.successGreen else InkTheme.colors.textFaint,
                    fontWeight = if (notification.earningsLabel != null) FontWeight.SemiBold else FontWeight.Normal,
                )
            }

            // Right content
            when {
                notification.coverEmoji != null && notification.coverGradient != null -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(40.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(notification.coverGradient)),
                    ) {
                        Text(notification.coverEmoji, style = InkTheme.typography.bodyLarge)
                    }
                }
                notification.actionLabel != null -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(GradientAccent))
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text       = notification.actionLabel,
                            style = InkTheme.typography.bodySmall,
                            color      = Color.White,
                        )
                    }
                }
                notification.badgeLabel != null && notification.badgeColor != null -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(notification.badgeColor.copy(alpha = 0.1f))
                            .border(0.5.dp, notification.badgeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text       = notification.badgeLabel,
                            style = InkTheme.typography.labelSmall,
                            color      = notification.badgeColor,
                        )
                    }
                }
            }
        }

        // Bottom divider
        HorizontalDivider(
            color     = InkTheme.colors.bgBorder.copy(alpha = 0.4f),
            thickness = 0.5.dp,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 67.dp),
        )
    }
}

@Composable
private fun NotifAvatar(notification: Notification) {
    val iconForType: Pair<ImageVector, Color> = when (notification.type) {
        NotifType.LIKE        -> Icons.Rounded.Favorite to InkTheme.colors.successGreen
        NotifType.COMMENT     -> Icons.Rounded.ChatBubble to InkTheme.colors.genreSciFi
        NotifType.FOLLOW      -> Icons.Rounded.PersonAdd to InkTheme.colors.accentPrimary
        NotifType.SALE        -> Icons.Rounded.AttachMoney to InkTheme.colors.successGreen
        NotifType.FEATURE     -> Icons.Rounded.Star to InkTheme.colors.starColor
        NotifType.MILESTONE   -> Icons.Rounded.EmojiEvents to InkTheme.colors.starColor
        NotifType.NEW_CHAPTER -> Icons.Rounded.Notifications to InkTheme.colors.accentLight
        NotifType.PAYOUT      -> Icons.Rounded.AccountBalance to InkTheme.colors.successGreen
    }

    Box {
        if (notification.actorInitials != null && notification.actorGradient != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(notification.actorGradient)),
            ) {
                Text(
                    text       = notification.actorInitials,
                    style = InkTheme.typography.titleMedium,
                    color      = Color.White,
                )
            }
        } else {
            // System notification avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.bgCard)
                    .border(0.5.dp, InkTheme.colors.bgBorder, CircleShape),
            ) {
                Icon(
                    imageVector        = iconForType.first,
                    contentDescription = null,
                    tint               = iconForType.second,
                    modifier           = Modifier.size(18.dp),
                )
            }
        }

        // Type badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 2.dp, y = 2.dp)
                .clip(CircleShape)
                .background(iconForType.second)
                .border(2.dp, InkTheme.colors.bgDeep, CircleShape),
        ) {
            Icon(
                imageVector        = iconForType.first,
                contentDescription = null,
                tint               = if (notification.type == NotifType.LIKE || notification.type == NotifType.SALE)
                    Color(0xFF0f2a1e) else Color.White,
                modifier = Modifier.size(9.dp),
            )
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(filter: NotifFilter) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
    ) {
        Icon(
            imageVector        = Icons.Rounded.NotificationsNone,
            contentDescription = null,
            tint               = InkTheme.colors.textFaint,
            modifier           = Modifier.size(52.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text       = "No ${filter.label.lowercase()} notifications",
            style = InkTheme.typography.titleLarge,
            color      = InkTheme.colors.textSecondary,
        )
        Text(
            text      = "You're all caught up for now.",
            style = InkTheme.typography.titleMedium,
            color     = InkTheme.colors.textMuted,
            modifier  = Modifier.padding(top = 4.dp),
        )
    }
}
