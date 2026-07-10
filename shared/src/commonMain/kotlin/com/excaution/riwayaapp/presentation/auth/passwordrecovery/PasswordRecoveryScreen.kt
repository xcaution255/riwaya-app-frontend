package com.excaution.riwayaapp.presentation.auth.passwordrecovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.components.AuthBackButton
import com.excaution.riwayaapp.presentation.components.AuthField
import com.excaution.riwayaapp.presentation.components.AuthFieldState
import com.excaution.riwayaapp.presentation.components.AuthHeader
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.components.PasswordStrengthRow
import com.excaution.riwayaapp.presentation.components.evaluatePassword
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme

@Composable
fun PasswordRecoveryScreen(
    onBack: () -> Unit,
    onPasswordSet: () -> Unit,
) {
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword
    val strength       = evaluatePassword(newPassword)

    val confirmState = when {
        confirmPassword.isEmpty()  -> AuthFieldState.IDLE
        passwordsMatch             -> AuthFieldState.VALID
        else                       -> AuthFieldState.ERROR
    }

    val requirements = listOf(
        "At least 8 characters"   to (newPassword.length >= 8),
        "One uppercase letter"    to newPassword.any { it.isUpperCase() },
        "One number or symbol"    to newPassword.any { it.isDigit() || !it.isLetterOrDigit() },
        "Passwords match"         to passwordsMatch,
    )

    val canSubmit = requirements.all { it.second }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AuthScaffold {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(260))) {
            AuthBackButton(onClick = onBack)
        }

        // Art – success ring style
        AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.7f) + fadeIn(tween(400, 60)),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp)) {
                SuccessRingArt(isComplete = canSubmit)
            }
        }

        // Tag pill
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(340, 90))) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                val pillColor by animateColorAsState(
                    targetValue   = if (canSubmit) InkTheme.colors.successGreen else InkTheme.colors.accentPrimary,
                    animationSpec = tween(300),
                    label         = "pillColor",
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillColor.copy(alpha = 0.1f))
                        .border(0.5.dp, pillColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = if (canSubmit) "READY TO SET" else "SET NEW PASSWORD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = pillColor,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }

        AnimatedVisibility(visible = visible, enter = fadeIn(tween(350, 110)) + slideInVertically(tween(350, 110)) { 16 }) {
            AuthHeader(
                tag      = "",
                title    = "Create new password",
                subtitle = "Make it strong and unique",
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 4.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(360, 130)) + slideInVertically(tween(360, 130)) { 20 }) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AuthField(
                        label         = "New password",
                        value         = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder   = "Min. 8 characters",
                        isPassword    = true,
                        keyboardType  = KeyboardType.Password,
                        imeAction     = ImeAction.Next,
                    )
                    if (newPassword.isNotEmpty()) {
                        PasswordStrengthRow(password = newPassword)
                    }
                }
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 150)) + slideInVertically(tween(370, 150)) { 20 }) {
                AuthField(
                    label         = "Confirm password",
                    value         = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder   = "Re-enter password",
                    state         = confirmState,
                    isPassword    = true,
                    keyboardType  = KeyboardType.Password,
                    imeAction     = ImeAction.Done,
                    trailingIcon  = when (confirmState) {
                        AuthFieldState.VALID -> Icons.Rounded.CheckCircle
                        AuthFieldState.ERROR -> Icons.Rounded.Error
                        else                 -> null
                    },
                )
            }

            // Requirements checklist
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(380, 170))) {
                PasswordRequirements(requirements = requirements)
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(390, 190))) {
                AuthPrimaryButton(
                    text      = "Set new password",
                    onClick   = { if (canSubmit) { isLoading = true; onPasswordSet() } },
                    icon      = Icons.Rounded.Security,
                    isLoading = isLoading,
                    gradient  = if (canSubmit) listOf(Color(0xFF22c55e), InkTheme.colors.successGreen)
                    else GradientAccent,
                )
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}