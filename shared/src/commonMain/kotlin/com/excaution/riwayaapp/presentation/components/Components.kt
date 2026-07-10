package com.excaution.riwayaapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgSurface
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.StarColor
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import com.excaution.riwayaapp.presentation.theme.TextSecondary


// ── Avatar / Initials Circle ─────────────────────────────────────────────────

@Composable
fun InitialsAvatar(
    initial: String,
    size: Dp = 36.dp,
    fontSize: Int = 13,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(GradientAccent))
            .border(1.5.dp, InkTheme.colors.accentLight.copy(alpha = 0.3f), CircleShape),
    ) {
        Text(
            text = initial.uppercase(),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

// ── Genre / Category Tag ─────────────────────────────────────────────────────

@Composable
fun GenreTag(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        letterSpacing = 0.8.sp,
        modifier = modifier,
    )
}

// ── Star Rating Row ──────────────────────────────────────────────────────────

@Composable
fun StarRating(
    rating: Float,
    reviewCount: String? = null,
    starSize: Int = 13,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { i ->
            Text(
                text = "★",
                fontSize = starSize.sp,
                color = if (i < rating.toInt()) InkTheme.colors.starColor
                        else if (i < rating) InkTheme.colors.starColor.copy(alpha = 0.4f)
                        else InkTheme.colors.bgBorder,
            )
        }
        if (reviewCount != null) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = "($reviewCount)",
                fontSize = 11.sp,
                color = InkTheme.colors.textMuted,
            )
        }
    }
}

// ── Animated Press Scale Button ──────────────────────────────────────────────

@Composable
fun PressScaleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue  = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale",
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick,
            ),
        content = content,
    )
}

// ── Gradient Primary Button ──────────────────────────────────────────────────

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    PressScaleButton(onClick = onClick, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(GradientAccent))
                .padding(vertical = 13.dp),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

// ── Icon Button ──────────────────────────────────────────────────────────────

@Composable
fun SurfaceIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = InkTheme.colors.textSecondary,
    badge: Int? = null,
) {
    Box(modifier = modifier.padding(end = 12.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(InkTheme.colors.bgSurface)
                .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(11.dp))
                .clickable(onClick = onClick),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
        }
        if (badge != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentPrimary)
                    .border(2.dp, InkTheme.colors.bgDeep, CircleShape),
            ) {
                Text(
                    text = badge.toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

// ── Stat Chip ────────────────────────────────────────────────────────────────

@Composable
fun StatChip(icon: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(icon, fontSize = 11.sp)
        Text(value, fontSize = 11.sp, color = TextFaint)
    }
}