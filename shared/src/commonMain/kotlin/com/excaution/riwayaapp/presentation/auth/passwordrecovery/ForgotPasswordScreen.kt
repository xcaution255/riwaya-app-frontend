package com.excaution.riwayaapp.presentation.auth.passwordrecovery

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.auth.login.LoginEvent
import com.excaution.riwayaapp.presentation.auth.login.LoginViewModel
import com.excaution.riwayaapp.presentation.auth.otp.OtpVerifyEvent
import com.excaution.riwayaapp.presentation.components.ArtIconWithRings
import com.excaution.riwayaapp.presentation.components.AuthBackButton
import com.excaution.riwayaapp.presentation.components.AuthDivider
import com.excaution.riwayaapp.presentation.components.AuthField
import com.excaution.riwayaapp.presentation.components.AuthFieldState
import com.excaution.riwayaapp.presentation.components.AuthGhostButton
import com.excaution.riwayaapp.presentation.components.AuthHeader
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.components.InfoHintBox
import com.excaution.riwayaapp.presentation.components.PasswordStrengthRow
import com.excaution.riwayaapp.presentation.components.evaluatePassword
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.BgInput
import com.excaution.riwayaapp.presentation.theme.GenreMystery
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onSendOtp: () -> Unit,
) {
    val viewModel: ForgotPasswordViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var email     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val emailState = when {
        email.isEmpty()     -> AuthFieldState.IDLE
        email.contains("@") -> AuthFieldState.VALID
        else                -> AuthFieldState.ERROR
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(Unit) {
        visible = true
        viewModel.events.collect { event ->
            when (event) {
                ForgotPasswordEvent.NavigateToPasswordRecovery -> onSendOtp()
            }
        }
    }


    AuthScaffold {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(260))) {
            AuthBackButton(onClick = onBack)
        }

        // Art
        AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.7f) + fadeIn(tween(400, 60)),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp)) {
                ArtIconWithRings(
                    icon        = Icons.Rounded.Lock,
                    iconTint    = InkTheme.colors.genreMystery,
                    borderColor = InkTheme.colors.genreMystery,
                    badge = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .clip(CircleShape)
                                .background(InkTheme.colors.genreMystery)
                                .border(2.dp, InkTheme.colors.bgDeep, CircleShape),
                        ) {
                            Text("?", style = InkTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                )
            }
        }

        // Tag pill
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(340, 90))) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(InkTheme.colors.genreMystery.copy(alpha = 0.08f))
                        .border(0.5.dp, InkTheme.colors.genreMystery.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text("ACCOUNT RECOVERY", style = InkTheme.typography.labelSmall, color = InkTheme.colors.genreMystery, letterSpacing = 1.sp)
                }
            }
        }

        AnimatedVisibility(visible = visible, enter = fadeIn(tween(350, 110)) + slideInVertically(tween(350, 110)) { 16 }) {
            AuthHeader(
                tag      = "",
                title    = "Forgot your password?",
                subtitle = "Enter your email and we'll send an OTP to reset your password",
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(360, 130)) + slideInVertically(tween(360, 130)) { 20 }) {
                AuthField(
                    label         = "Email address",
                    value         = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder   = "you@example.com",
                    state         = emailState,
                    keyboardType  = KeyboardType.Email,
                    trailingIcon  = Icons.Rounded.AlternateEmail,
                    imeAction     = ImeAction.Go,
//                    onImeAction   = { if (email.contains("@")) { isLoading = true; viewModel.forgotPassword() } },
                )
            }

            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 150))) {
                AuthPrimaryButton(
                    text      = "Get OTP",
                    onClick   = { isLoading = true; viewModel.forgotPassword() },
                    icon      = Icons.Rounded.Send,
                    isLoading = isLoading,
                )
            }
            uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// ── Requirements checklist ────────────────────────────────────────────────────

@Composable
fun PasswordRequirements(requirements: List<Pair<String, Boolean>>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InkTheme.colors.bgCard)
            .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        requirements.forEach { (label, met) ->
            val color by animateColorAsState(
                targetValue   = if (met) InkTheme.colors.successGreen else InkTheme.colors.textFaint,
                animationSpec = tween(250),
                label         = "reqColor-$label",
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = if (met) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint   = color,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text       = label,
                    style = InkTheme.typography.bodySmall,
                    color      = if (met) InkTheme.colors.textPrimary else InkTheme.colors.textMuted,
                    fontWeight = if (met) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

// ── Animated success ring ─────────────────────────────────────────────────────

@Composable
fun SuccessRingArt(isComplete: Boolean) {
    val ringColor by animateColorAsState(
        targetValue   = if (isComplete) InkTheme.colors.successGreen else InkTheme.colors.accentPrimary,
        animationSpec = tween(400),
        label         = "ringColor",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp),
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, ringColor, CircleShape),
        )
        // Inner fill
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(ringColor.copy(alpha = 0.1f)),
        ) {
            Icon(
                imageVector = if (isComplete) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                contentDescription = null,
                tint     = ringColor,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

// ── Animated checkbox (used in Register) ─────────────────────────────────────

@Composable
fun AnimatedCheckbox(checked: Boolean, onToggle: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue   = if (checked) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "checkScale",
    )
    val bgColor by animateColorAsState(
        targetValue   = if (checked) InkTheme.colors.accentPrimary else InkTheme.colors.bgInput,
        animationSpec = tween(200),
        label         = "checkBg",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (checked) InkTheme.colors.accentPrimary else InkTheme.colors.bgBorder,
        animationSpec = tween(200),
        label         = "checkBorder",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(18.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(5.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(5.dp))
            .clickable(onClick = onToggle),
    ) {
        AnimatedVisibility(
            visible = checked,
            enter   = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit    = scaleOut() + fadeOut(),
        ) {
            Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
        }
    }
}