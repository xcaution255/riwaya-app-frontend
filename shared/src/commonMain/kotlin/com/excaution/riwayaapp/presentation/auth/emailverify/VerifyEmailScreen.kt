package com.excaution.riwayaapp.presentation.auth.emailverify

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.components.ArtIconWithRings
import com.excaution.riwayaapp.presentation.components.AuthBackButton
import com.excaution.riwayaapp.presentation.components.AuthGhostButton
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.theme.AccentLight
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgBorder
import com.excaution.riwayaapp.presentation.theme.BgCard
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.SuccessGreen
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted
import com.excaution.riwayaapp.presentation.theme.TextPrimary


@Composable
fun VerifyEmailScreen(
    email: String,
    onBack: () -> Unit,
    onEnterOtp: () -> Unit,
    onResend: () -> Unit = {},
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AuthScaffold {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(260))) {
            AuthBackButton(onClick = onBack)
        }

        // Animated art
        AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.7f) + fadeIn(tween(400, 60)),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ArtIconWithRings(
                        icon        = Icons.Rounded.MarkEmailRead,
                        iconTint    = InkTheme.colors.accentLight,
                        borderColor = InkTheme.colors.accentPrimary,
                        badge = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .clip(CircleShape)
                                    .background(InkTheme.colors.successGreen)
                                    .border(2.dp, InkTheme.colors.bgDeep, CircleShape),
                            ) {
                                Icon(Icons.Rounded.Check, null, tint = Color(0xFF0f2a1e), modifier = Modifier.size(11.dp))
                            }
                        }
                    )
                }
            }
        }

        // Tag pill
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(350, 100))) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(InkTheme.colors.accentPrimary.copy(alpha = 0.1f))
                        .border(0.5.dp, InkTheme.colors.accentPrimary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text("EMAIL SENT", style = InkTheme.typography.labelSmall, color = InkTheme.colors.accentLight, letterSpacing = 1.sp)
                }
            }
        }

        // Title block (centered)
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(360, 120)) + slideInVertically(tween(360, 120)) { 16 }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Check your inbox",
                    style = InkTheme.typography.headlineMedium,
                    color = InkTheme.colors.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = InkTheme.colors.textMuted, fontSize = 13.sp)) { append("We sent a verification link to\n") }
                        withStyle(SpanStyle(color = InkTheme.colors.accentLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)) { append(email) }
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        }

        // Info card
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 150)) + slideInVertically(tween(370, 150)) { 20 }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(InkTheme.colors.bgCard)
                    .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp)),
            ) {
                InfoCardRow(
                    icon    = Icons.Rounded.AccessTime,
                    iconBg  = InkTheme.colors.accentPrimary.copy(alpha = 0.1f),
                    iconTint = InkTheme.colors.accentPrimary,
                    title   = "Link expires in 24 hours",
                    subtitle = "From time of sending",
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(InkTheme.colors.bgBorder).padding(start = 54.dp))
                InfoCardRow(
                    icon    = Icons.Rounded.ShieldMoon,
                    iconBg  = InkTheme.colors.successGreen.copy(alpha = 0.1f),
                    iconTint = InkTheme.colors.successGreen,
                    title   = "Check spam folder",
                    subtitle = "If not visible within 1 minute",
                )
            }
        }

        // Actions
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 14.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(380, 180))) {
                AuthGhostButton(text = "Resend email", onClick = onResend, icon = Icons.Rounded.Refresh)
            }
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(390, 200))) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = InkTheme.colors.textFaint, fontSize = 12.sp)) { append("Wrong email? ") }
                            withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)) { append("Change it") }
                        },
                        onClick = { onBack() },
                    )
                }
            }
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(400, 220))) {
                AuthPrimaryButton(text = "Enter OTP instead", onClick = onEnterOtp, icon = Icons.Rounded.PhoneAndroid)
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun InfoCardRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(iconBg),
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(title, style = InkTheme.typography.bodySmall, color = InkTheme.colors.textPrimary)
            Text(subtitle, style = InkTheme.typography.labelSmall, color = InkTheme.colors.textMuted)
        }
    }
}