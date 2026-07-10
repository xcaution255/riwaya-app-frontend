package com.excaution.riwayaapp.presentation.auth.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextPrimary
import kotlinx.coroutines.delay

// ── How long the splash shows before auto-navigating ─────────────────────────
private const val SPLASH_DURATION_MS = 2600L

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── Progress animation
    val progress by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(
            durationMillis = SPLASH_DURATION_MS.toInt() - 300,
            delayMillis    = 400,
            easing         = FastOutSlowInEasing,
        ),
        label = "splashProgress",
    )

    // ── Logo entrance ─────────────────────────────────────────────────────────
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var barVisible  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100); logoVisible = true
        delay(280); textVisible = true
        delay(200); barVisible  = true
        delay(SPLASH_DURATION_MS); onFinished()
    }

    // ── Rotating ring ─────────────────────────────────────────────────────────
    val ringRotation by rememberInfiniteTransition(label = "ring").animateFloat(
        initialValue   = 0f,
        targetValue    = 360f,
        animationSpec  = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label          = "ringRot",
    )

    // ── Particle pulse ────────────────────────────────────────────────────────
    val particlePulse by rememberInfiniteTransition(label = "particle").animateFloat(
        initialValue  = 0.4f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label         = "pPulse",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(InkTheme.colors.bgDeep),
    ) {
        // ── Ambient background rings ──────────────────────────────────────────
        SplashRing(size = 360.dp, alpha = 0.07f)
        SplashRing(size = 240.dp, alpha = 0.09f)
        SplashRing(size = 140.dp, alpha = 0.12f)

        // 3. FIXED: Dynamically resolved vector ring color
        val ringColor = InkTheme.colors.accentPrimary.copy(alpha = 0.15f)
        // ── Rotating dashed accent ring ───────────────────────────────────────
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .rotate(ringRotation),
        ) {
            val stroke = Stroke(
                width = 1.dp.toPx(),
                cap   = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(6f, 18f), 0f
                )
            )
            drawCircle(
                color  = ringColor,
                radius = size.minDimension / 2f,
                style  = stroke,
            )
        }

        // ── Floating particles ────────────────────────────────────────────────
        val particles = remember {
            listOf(
                Triple(0.22f, 0.20f, 4.dp),
                Triple(0.78f, 0.18f, 3.dp),
                Triple(0.15f, 0.70f, 5.dp),
                Triple(0.80f, 0.72f, 3.dp),
                Triple(0.50f, 0.12f, 2.dp),
                Triple(0.10f, 0.45f, 2.dp),
            )
        }
        // 4. FIXED: Dynamically resolved values fetched out of your custom scope
        val aPrimary = InkTheme.colors.accentPrimary
        val aLight = InkTheme.colors.accentLight
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            particles.forEachIndexed { i, (xFrac, yFrac, size) ->
                val delay    = i * 0.3f
                val alphaMod = ((particlePulse + delay) % 1f)
                Box(
                    modifier = Modifier
                        .offset(
                            x = maxWidth * xFrac - size / 2,
                            y = maxHeight * yFrac - size / 2,
                        )
                        .size(size)
                        .clip(CircleShape)
                        .alpha(alphaMod.coerceIn(0.2f, 0.9f))
                        .background(
                            if (i % 2 == 0) aPrimary else aLight
                        ),
                )
            }
        }

        // ── Logo + wordmark ───────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(
                visible = logoVisible,
                enter   = scaleIn(
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    initialScale = 0.6f,
                ) + fadeIn(tween(400)),
            ) {
                LogoIcon()
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = textVisible,
                enter   = slideInVertically(tween(400)) { it / 2 } + fadeIn(tween(400)),
            ) {
                // 5. FIXED: Texts now track dark / light adjustments smoothly
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = InkTheme.colors.textPrimary)) { append("Riwaya") }
                            withStyle(SpanStyle(color = InkTheme.colors.accentPrimary)) { append("App") }
                        },
                        fontSize      = 32.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text          = "STORIES · COMMUNITY · BOOKS",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Medium,
                        color         = InkTheme.colors.textFaint,
                        letterSpacing = 1.6.sp,
                    )
                }
            }
        }

        // ── Loading bar ───────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = barVisible,
            enter   = fadeIn(tween(500)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 64.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(InkTheme.colors.bgBorder),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(1.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(InkTheme.colors.accentPrimary,
                                        InkTheme.colors.accentLight))
                            ),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text     = "Preparing your world…",
                    fontSize = 11.sp,
                    color    = InkTheme.colors.textFaint,
                    letterSpacing = 0.3.sp,
                )
            }
        }
    }
}

// ── Logo Icon ─────────────────────────────────────────────────────────────────

@Composable
private fun LogoIcon() {
    val shimmer by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue  = -1f,
        targetValue   = 2f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label         = "shimmerPos",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(InkTheme.colors.bgCard)
            .background(
                Brush.linearGradient(
                    listOf(
                        InkTheme.colors.accentPrimary.copy(alpha = 0.18f),
                        InkTheme.colors.accentLight.copy(alpha = 0.08f),
                    )
                )
            ),
    ) {
        // Shimmer sweep
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            InkTheme.colors.accentPrimary.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                        start = Offset(shimmer * 200, 0f),
                        end   = Offset(shimmer * 200 + 80, 80f),
                    )
                ),
        )
        // Pen icon (use whichever icon lib you have; here: Unicode fallback)
        Text("✍", fontSize = 36.sp)
    }
}

// ── Ambient Ring ──────────────────────────────────────────────────────────────

@Composable
private fun SplashRing(size: Dp, alpha: Float) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Transparent)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color.Transparent,
                        InkTheme.colors.accentPrimary.copy(alpha = alpha),
                        Color.Transparent,
                    ),
                    radius = size.value * 2,
                )
            ),
    )
}