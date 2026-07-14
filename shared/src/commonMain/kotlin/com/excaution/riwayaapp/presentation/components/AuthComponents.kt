package com.excaution.riwayaapp.presentation.components


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgInput
import com.excaution.riwayaapp.presentation.theme.DangerRed
import com.excaution.riwayaapp.presentation.theme.GenreMystery
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import com.excaution.riwayaapp.presentation.theme.TextSecondary

enum class AuthFieldState { IDLE, FOCUSED, VALID, ERROR }

@Composable
fun AuthBrandHeader(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 24.dp, vertical = 14.dp),
    ) {
        // Wordmark
        Row {
            Text("Riwaya", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.textPrimary, letterSpacing = (-0.6).sp)
            Text("App", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkTheme.colors.accentPrimary, letterSpacing = (-0.6).sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  SECTION HEADER (tag + title + subtitle)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthHeader(
    tag: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    tagColor: Color = AccentPrimary,
) {
    Column(
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Text(
            text          = tag,
            color         = tagColor,
            style = InkTheme.typography.labelSmall
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text          = title,
            fontSize      = 22.sp,
            style = InkTheme.typography.displayLarge,
            color         = InkTheme.colors.textPrimary,
            textAlign     = if (centered) TextAlign.Center else TextAlign.Start,
        )
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text      = subtitle,
                style = InkTheme.typography.titleMedium,
                color     = InkTheme.colors.textMuted,
                textAlign  = if (centered) TextAlign.Center else TextAlign.Start,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  AUTH TEXT FIELD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    state: AuthFieldState = AuthFieldState.IDLE,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    trailingIcon: ImageVector? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val resolvedState = when {
        isFocused            -> AuthFieldState.FOCUSED
        else                 -> state
    }

    val borderColor by animateColorAsState(
        targetValue = when (resolvedState) {
            AuthFieldState.FOCUSED -> InkTheme.colors.accentPrimary
            AuthFieldState.VALID   -> InkTheme.colors.successGreen
            AuthFieldState.ERROR   -> InkTheme.colors.dangerRed
            AuthFieldState.IDLE    -> InkTheme.colors.bgBorder
        },
        animationSpec = tween(200),
        label = "fieldBorder",
    )
    val bgColor by animateColorAsState(
        targetValue = when (resolvedState) {
            AuthFieldState.FOCUSED -> InkTheme.colors.bgFocused
            AuthFieldState.VALID   -> InkTheme.colors.bgValid
            AuthFieldState.ERROR   -> InkTheme.colors.bgError
            AuthFieldState.IDLE    -> InkTheme.colors.bgInput
        },
        animationSpec = tween(200),
        label = "fieldBg",
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier,
    ) {
        Text(
            text          = label.uppercase(),
            style = InkTheme.typography.labelSmall,
            color         = InkTheme.colors.textMuted,
        )
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            singleLine    = true,
            textStyle = TextStyle(
                color      = InkTheme.colors.textPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction    = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() },
                onGo   = { onImeAction() },
            ),
            cursorBrush = SolidColor(InkTheme.colors.accentPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { inner ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(placeholder,  style = InkTheme.typography.bodyMedium ,color = InkTheme.colors.textFaint)
                        }
                        inner()
                    }
                    if (isPassword) {
                        IconButton(
                            onClick  = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(18.dp),
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff
                                else Icons.Rounded.Visibility,
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint     = if (resolvedState == AuthFieldState.FOCUSED) InkTheme.colors.accentPrimary else InkTheme.colors.textFaint,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    } else if (trailingIcon != null) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = when (resolvedState) {
                                AuthFieldState.VALID  -> InkTheme.colors.successGreen
                                AuthFieldState.ERROR  -> InkTheme.colors.dangerRed
                                AuthFieldState.FOCUSED -> InkTheme.colors.accentPrimary
                                else                  -> InkTheme.colors.textFaint
                            },
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PASSWORD STRENGTH INDICATOR
// ─────────────────────────────────────────────────────────────────────────────

enum class PasswordStrength(val label: String, val color: Color, val bars: Int) {
    WEAK    ("Weak",     DangerRed,   1),
    FAIR    ("Fair", GenreMystery, 2),
    STRONG  ("Strong",  SuccessGreen, 3),
    VERY_STRONG("Very strong", SuccessGreen, 4),
}

fun evaluatePassword(pw: String): PasswordStrength? {
    if (pw.isEmpty()) return null
    var score = 0
    if (pw.length >= 8)  score++
    if (pw.any { it.isUpperCase() }) score++
    if (pw.any { it.isDigit() })     score++
    if (pw.any { !it.isLetterOrDigit() }) score++
    return when (score) {
        1    -> PasswordStrength.WEAK
        2    -> PasswordStrength.FAIR
        3    -> PasswordStrength.STRONG
        else -> PasswordStrength.VERY_STRONG
    }
}

@Composable
fun PasswordStrengthRow(password: String) {
    val strength = evaluatePassword(password) ?: return
    val targetWidth by animateFloatAsState(
        targetValue   = strength.bars / 4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "pwStrength",
    )
    val barColor by animateColorAsState(
        targetValue   = strength.color,
        animationSpec = tween(300),
        label         = "pwColor",
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            repeat(4) { i ->
                val filled = i < strength.bars
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(if (filled) barColor else InkTheme.colors.bgBorder),
                )
            }
        }
        Text(
            text       = strength.label,
            style = InkTheme.typography.labelSmall,
            color      = strength.color,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  GRADIENT PRIMARY BUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    gradient: List<Color> = GradientAccent,
) {
    PressScaleButton(onClick = { if (!isLoading) onClick() }, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient)),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = InkTheme.typography.titleMedium,
                    color = Color.White,
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  GHOST BUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    PressScaleButton(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp)),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = InkTheme.colors.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = InkTheme.typography.titleMedium,
                color = InkTheme.colors.textSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DIVIDER "or"
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthDivider(label: String = "or continue with") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = InkTheme.colors.bgBorder, thickness = 0.5.dp)
        Text(label, style = InkTheme.typography.bodySmall, color = InkTheme.colors.textFaint)
        HorizontalDivider(modifier = Modifier.weight(1f), color = InkTheme.colors.bgBorder, thickness = 0.5.dp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  SOCIAL BUTTONS ROW
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SocialButtonsRow(onGoogle: () -> Unit = {}, onApple: () -> Unit = {}) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SocialButton(label = "Google", onClick = onGoogle, modifier = Modifier.weight(1f))
        SocialButton(label = "Apple",  onClick = onApple,  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SocialButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PressScaleButton(onClick = onClick, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(12.dp)),
        ) {
            Icon(
                imageVector = if (label == "Google") Icons.Rounded.Language else Icons.Rounded.Apps,
                contentDescription = label,
                tint = if (label == "Google") InkTheme.colors.dangerRed else InkTheme.colors.textPrimary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(label, style = InkTheme.typography.titleMedium, color = InkTheme.colors.textSecondary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ANIMATED ART ICON (pulsing rings)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ArtIconWithRings(
    icon: ImageVector,
    iconTint: Color = InkTheme.colors.accentLight,
    borderColor: Color = InkTheme.colors.accentPrimary,
    badge: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val pulse1 by rememberInfiniteTransition(label = "p1").animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOut), RepeatMode.Reverse),
        label         = "pulse1",
    )
    val pulse2 by rememberInfiniteTransition(label = "p2").animateFloat(
        initialValue  = 0.7f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2200, delayMillis = 400, easing = EaseInOut), RepeatMode.Reverse),
        label         = "pulse2",
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    1.dp,
                    borderColor.copy(alpha = 0.07f * pulse2),
                    RoundedCornerShape(28.dp),
                ),
        )
        // Middle ring
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    1.dp,
                    borderColor.copy(alpha = 0.13f * pulse1),
                    RoundedCornerShape(22.dp),
                ),
        )
        // Icon box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(InkTheme.colors.bgCard)
                .border(0.5.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
        }
        // Badge overlay
        badge?.invoke()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ERROR BANNER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ErrorBanner(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(InkTheme.colors.dangerRed.copy(alpha = 0.07f))
            .border(0.5.dp, InkTheme.colors.dangerRed.copy(alpha = 0.25f), RoundedCornerShape(11.dp))
            .padding(horizontal = 13.dp, vertical = 10.dp),
    ) {
        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = InkTheme.colors.dangerRed, modifier = Modifier.size(16.dp))
        Text(message, style = InkTheme.typography.bodySmall, color = InkTheme.colors.dangerRed)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  INFO HINT BOX
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun InfoHintBox(
    text: String,
    tint: Color = GenreMystery,
    bgAlpha: Float = 0.06f,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(tint.copy(alpha = bgAlpha))
            .border(0.5.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(11.dp))
            .padding(horizontal = 13.dp, vertical = 10.dp),
    ) {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp).padding(top = 1.dp))
        Text(text, style = InkTheme.typography.bodySmall, color = InkTheme.colors.textMuted)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  BACK BUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthBackButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(22.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(InkTheme.colors.bgCard)
            .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .size(40.dp)
    ) {
        Icon(Icons.Rounded.ArrowBackIosNew, null, tint = InkTheme.colors.textSecondary, modifier = Modifier.size(12.dp).offset(x = 2.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  AUTH SCREEN SCAFFOLD (handles shared bg + status bar)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .background(InkTheme.colors.bgDeep)
            .verticalScroll(rememberScrollState()),
        content = content,
    )
}

private val EaseInOut = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)