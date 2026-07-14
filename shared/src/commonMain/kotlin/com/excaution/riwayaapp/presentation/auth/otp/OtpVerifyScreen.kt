package com.excaution.riwayaapp.presentation.auth.otp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.auth.register.RegisterEvent
import com.excaution.riwayaapp.presentation.components.ArtIconWithRings
import com.excaution.riwayaapp.presentation.components.AuthBackButton
import com.excaution.riwayaapp.presentation.components.AuthGhostButton
import com.excaution.riwayaapp.presentation.components.AuthHeader
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.components.ErrorBanner
import com.excaution.riwayaapp.presentation.components.InfoHintBox
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgInput
import com.excaution.riwayaapp.presentation.theme.DangerRed
import com.excaution.riwayaapp.presentation.theme.GenreSciFi
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private const val OTP_LENGTH = 6

@Composable
fun OtpVerifyScreen(
    emailHint: String,
    onBack: () -> Unit,
    onVerified: () -> Unit,
) {

    val viewModel: OtpVerifyViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var otpValue  by remember { mutableStateOf("") }
    var hasError  by remember { mutableStateOf(false) }
    var timerSecs by remember { mutableIntStateOf(42) }
    var isVerifying by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Countdown timer
    LaunchedEffect(timerSecs) {
        if (timerSecs > 0) {
            delay(1000)
            timerSecs--
        }
    }

    // Auto-verify when 6 digits entered
    LaunchedEffect(otpValue) {
        if (otpValue.length == OTP_LENGTH) {
            hasError  = false
            isVerifying = true
            delay(800)
            viewModel.events.collect { event ->
                when (event) {
                    OtpVerifyEvent.NavigateToHome(uiState.email) -> {onVerified()}
                    else -> {}
                }
            }
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(400)
        focusRequester.requestFocus()
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
                    icon        = Icons.Rounded.PhoneAndroid,
                    iconTint    = InkTheme.colors.genreSciFi,
                    borderColor = InkTheme.colors.genreSciFi,
                )
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.7f) + fadeIn(tween(400, 60)),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp)) {
        AuthHeader(
            tag = "",
            title = "Enter OTP",
            subtitle = buildAnnotatedString {
                append("Sent to ")
                withStyle(SpanStyle(color = InkTheme.colors.genreSciFi, fontWeight = FontWeight.Bold)) { append(emailHint) }
            }.toString(),
            tagColor = InkTheme.colors.genreSciFi,
            centered = true,
        )
            }
        }

        // OTP boxes
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(360, 130)) + slideInVertically(tween(360, 130)) { 20 }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            ) {
                // Hidden text field for keyboard
                BasicTextField(
                    value         = uiState.otp,
                    onValueChange = { new ->
                        if (new.length <= OTP_LENGTH && new.all { it.isDigit() }) {
                            viewModel.onOtpChange(new)
                            hasError = false
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction    = ImeAction.Done,
                    ),
                    textStyle    = TextStyle(color = Color.Transparent, fontSize = 1.sp),
                    cursorBrush  = SolidColor(Color.Transparent),
                    modifier     = Modifier.size(1.dp).alpha(0f).focusRequester(focusRequester),
                    decorationBox = { it() },
                )
                // Visual boxes
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    repeat(OTP_LENGTH) { index ->
                        OtpDigitBox(
                            digit      = otpValue.getOrNull(index)?.toString() ?: "",
                            isFilled   = index < otpValue.length,
                            isCurrent  = index == otpValue.length,
                            hasError   = hasError,
                            isSuccess  = isVerifying && otpValue.length == OTP_LENGTH,
                        )
                    }
                }
            }
        }

        // Error
        AnimatedVisibility(
            visible = hasError,
            enter   = fadeIn(tween(200)) + expandVertically(tween(200)),
            exit    = fadeOut(tween(200)) + shrinkVertically(tween(200)),
        ) {
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)) {
                ErrorBanner("Invalid code. Please check and try again.")
            }
        }

        // Actions
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(380, 180))) {
                AuthPrimaryButton(
                    text      = if (isVerifying) "Verifying…" else "Verify code",
                    onClick   = {
                        if (otpValue.length < OTP_LENGTH) hasError = true
                        else { isVerifying = true; onVerified() }
                    },
                    icon      = Icons.Rounded.CheckCircle,
                    isLoading = isVerifying,
                )
            }
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(400, 220))) {
                InfoHintBox(
                    text = "Code expires in 10 minutes. Make sure your phone has signal.",
                    tint = InkTheme.colors.genreSciFi,
                )
            }

            // Timer row
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 160))) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                ) {
                    if (timerSecs > 0) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = InkTheme.colors.textFaint, fontSize = 13.sp)) { append("Didn't receive it? ") }
                                withStyle(SpanStyle(color = InkTheme.colors.accentLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                                    val m = timerSecs / 60
                                    val s = timerSecs % 60
                                    append("Resend in ${m}:${s.toString().padStart(2,'0')}")
                                }
                            }
                        )
                    } else {
                        ClickableText(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = InkTheme.colors.textFaint, fontSize = 13.sp)) { append("Didn't receive it? ") }
                                withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)) { append("Resend code") }
                            },
                            onClick = { timerSecs = 60 },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ── Single OTP digit box ──────────────────────────────────────────────────────

@Composable
private fun OtpDigitBox(
    digit: String,
    isFilled: Boolean,
    isCurrent: Boolean,
    hasError: Boolean,
    isSuccess: Boolean,
) {
    val cursorAlpha by rememberInfiniteTransition(label = "cursor").animateFloat(
        initialValue  = 1f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            tween(500, easing = StepEasing),
            RepeatMode.Reverse,
        ),
        label = "cursorAlpha",
    )

    val scale by animateFloatAsState(
        targetValue   = if (isFilled) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "digitScale",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            hasError  -> InkTheme.colors.dangerRed
            isSuccess -> InkTheme.colors.successGreen
            isCurrent -> InkTheme.colors.accentPrimary
            isFilled  -> InkTheme.colors.accentPrimary.copy(alpha = 0.6f)
            else      -> InkTheme.colors.bgBorder
        },
        animationSpec = tween(200),
        label         = "digitBorder",
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isSuccess -> InkTheme.colors.successGreen.copy(alpha = 0.1f)
            hasError  -> InkTheme.colors.dangerRed.copy(alpha = 0.08f)
            isFilled || isCurrent -> InkTheme.colors.bgOtp
            else      -> InkTheme.colors.bgInput
        },
        animationSpec = tween(200),
        label         = "digitBg",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 42.dp, height = 52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(12.dp)),
    ) {
        if (digit.isNotEmpty()) {
            Text(
                text       = digit,
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = if (isSuccess) InkTheme.colors.successGreen else InkTheme.colors.accentLight,
                textAlign  = TextAlign.Center,
            )
        } else if (isCurrent) {
            // Blinking cursor
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .alpha(cursorAlpha)
                    .background(InkTheme.colors.accentPrimary),
            )
        }
    }
}

private val StepEasing = Easing { f -> if (f < 0.5f) 0f else 1f }