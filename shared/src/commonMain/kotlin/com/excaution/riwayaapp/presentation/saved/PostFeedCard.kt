package com.excaution.riwayaapp.presentation.saved

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.data.post.PostResponse
import com.excaution.riwayaapp.format
import com.excaution.riwayaapp.presentation.home.PostViewModel
import com.excaution.riwayaapp.presentation.theme.InkTheme
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeedCard(
    post: PostResponse,
    modifier: Modifier = Modifier,
    onCommentsClick: (PostResponse) -> Unit = {}
) {
    val viewModel : PostSavedViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }
    var liked      by remember { mutableStateOf(post.likedByCurrentUser) }
    var saved      by remember { mutableStateOf(post.savedByCurrentUser) }
    var likeCount  by remember { mutableLongStateOf(post.likeCount ) }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(InkTheme.colors.bgDeep)
            .border(BorderStroke(0.dp, Color.Transparent)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {

//            // ── Meta row: genre pill + live badge + author ────────────────────
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
//            ) {
//                // Genre pill
//                GenrePill(genre = post.genre)
//                Spacer(Modifier.weight(1f))
//                //MetaChip(Icons.Rounded.Visibility, item.views)
//            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text       = post.title,
                style = InkTheme.typography.headlineMedium,
                color      = InkTheme.colors.textPrimary,
                modifier   = Modifier.padding(bottom = 7.dp),
            )

            // ── Body (collapsed / expanded) ───────────────────────────────────
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(180))
                },
                label = "bodyExpand-${post.id}",
            ) { expanded ->
                if (expanded) {
                    // Full story body
                    Text(
                        text = post.content,
                        style = InkTheme.typography.bodyLarge,
                        color  = InkTheme.colors.textSecondary,
                        modifier = Modifier
                            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { isExpanded = !isExpanded }
                    )
                } else {
                    // Preview with fade + Read more
                    Column {
                        Box {
                            Text(
                                text       = post.content,//later take some words
                                style = InkTheme.typography.bodyLarge,
                                color      = InkTheme.colors.textSecondary,
                                maxLines   = 3,
                                overflow   = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable { isExpanded = true }
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
                            text       = "Read more",
                            style = InkTheme.typography.labelSmall,
                            color      = InkTheme.colors.accentLight,
                            modifier   = Modifier
                                .padding(top = 4.dp)
                                .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { isExpanded = true },
                        )
                    }
                }
            }

            // ── Chapter info row ──────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .padding(top = 10.dp),
            ) {
                EngageChip(
                    icon = if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    label = formatCount(likeCount),
                    tint = if (liked) Color(0xFFE24B4A) else InkTheme.colors.textMuted,
                    modifier = Modifier,
                    onClick = {
                        viewModel.likePost(post.id)
                        liked = !liked
                        likeCount = post.likeCount}
                )
                EngageChip(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    label = "123",//post.commentCount,
                    modifier = Modifier,
                    onClick = {onCommentsClick(post)}
                )
                EngageChip(
                    icon = if (saved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    label = if (saved) "Saved" else "Save",
                    tint = if (saved) InkTheme.colors.accentPrimary else InkTheme.colors.textMuted,
                    modifier = Modifier,
                    onClick  = {
                        viewModel.savePost(post.id)
                        saved = !saved },
                )

                Spacer(Modifier.weight(1f))
                Text(viewModel.formatTime(post.createdAt.toString()), style = InkTheme.typography.bodySmall, color = InkTheme.colors.textFaint)

            }
        }
        // ── Divider ───────────────────────────────────────────────────────────
        HorizontalDivider(color = InkTheme.colors.bgBorder, thickness = 0.5.dp)

    }
}

//@Composable
//private fun GenrePill(genre: PostGenre) {
//    Box(
//        modifier = Modifier
//            .clip(CircleShape)
//            .background(genre.color.copy(alpha = genre.bgAlpha))
//            .border(0.5.dp, genre.color.copy(alpha = 0.3f), CircleShape)
//            .padding(horizontal = 10.dp, vertical = 4.dp),
//    ) {
//        Text(
//            text = genre.label.uppercase(),
//            style = InkTheme.typography.labelSmall,
//            color = genre.color
//        )
//    }
//}

//@Composable
//private fun MetaChip(icon: ImageVector, label: String) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(4.dp),
//    ) {
//        Icon(icon, null, tint = InkTheme.colors.textFaint, modifier = Modifier.size(13.dp))
//        Text(label, style = InkTheme.typography.bodySmall, color = InkTheme.colors.textFaint)
//    }
//}

@Composable
private fun EngageChip(
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) {onClick()}
            .scale(scale)

    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(13.dp))
        Text(label, style = InkTheme.typography.labelSmall, color = tint)

    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatCount(n: Long): String = when {
    // Cast the math result to a Double first, then call your extension function
    n >= 1_000_000 -> (n / 1_000_000.0).format(decimals = 1) + "M"
    n >= 1_000     -> (n / 1_000.0).format(decimals = 1) + "k"
    else           -> n.toString()
}