package com.excaution.riwayaapp.presentation.auth.register


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.auth.passwordrecovery.AnimatedCheckbox
import com.excaution.riwayaapp.presentation.components.AuthBrandHeader
import com.excaution.riwayaapp.presentation.components.AuthDivider
import com.excaution.riwayaapp.presentation.components.AuthField
import com.excaution.riwayaapp.presentation.components.AuthFieldState
import com.excaution.riwayaapp.presentation.components.AuthHeader
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.components.PasswordStrengthRow
import com.excaution.riwayaapp.presentation.components.SocialButtonsRow
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.TextFaint
import com.excaution.riwayaapp.presentation.theme.TextMuted

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var agreed    by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val emailState = when {
        email.isEmpty()          -> AuthFieldState.IDLE
        email.contains("@")      -> AuthFieldState.VALID
        else                     -> AuthFieldState.ERROR
    }

    // Staggered entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AuthScaffold {
//        // Brand
//        AnimatedVisibility(visible = visible, enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -10 }) {
//            AuthBrandHeader()
//        }

        // Header
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(320, 50)) + slideInVertically(tween(320, 50)) { 20 }) {
            AuthHeader(
                tag      = "",
                title    = "Join RiwayaApp",
                subtitle = "Explore books, share, and comment from stories",
            )
        }

        // Form
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            // Name row
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(340, 80)) + slideInVertically(tween(340, 80)) { 20 }) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AuthField(
                        label         = "Username",
                        value         = username,
                        onValueChange = { username = it },
                        placeholder   = "augustino",
                        modifier      = Modifier.weight(1f),
                        imeAction     = ImeAction.Next,
                    )
                }
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(350, 110)) + slideInVertically(tween(350, 110)) { 20 }) {
                AuthField(
                    label         = "Email address",
                    value         = email,
                    onValueChange = { email = it },
                    placeholder   = "you@example.com",
                    state         = emailState,
                    keyboardType  = KeyboardType.Email,
                    trailingIcon  = when (emailState) {
                        AuthFieldState.VALID -> Icons.Rounded.CheckCircle
                        AuthFieldState.ERROR -> Icons.Rounded.Error
                        else                 -> Icons.Rounded.AlternateEmail
                    },
                    imeAction = ImeAction.Next,
                )
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(360, 140)) + slideInVertically(tween(360, 140)) { 20 }) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AuthField(
                        label         = "Password",
                        value         = password,
                        onValueChange = { password = it },
                        placeholder   = "Min. 8 characters",
                        isPassword    = true,
                        keyboardType  = KeyboardType.Password,
                        imeAction     = ImeAction.Done,
                    )
                    if (password.isNotEmpty()) {
                        PasswordStrengthRow(password = password)
                    }
                }
            }

            // Terms checkbox
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 170)) + slideInVertically(tween(370, 170)) { 20 }) {
                TermsCheckbox(checked = agreed, onCheck = { agreed = it })
            }
        }

        Spacer(Modifier.height(20.dp))

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(380, 200))) {
                AuthPrimaryButton(
                    text      = "Create account",
                    onClick   = {
                        isLoading = true
                        onRegisterSuccess()
                    },
                    icon      = Icons.Rounded.PersonAdd,
                    isLoading = isLoading,
                )
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(410, 260))) {
                LinkRow(
                    text      = "Already have an account?",
                    linkLabel = "Sign in",
                    onClick   = onNavigateToLogin,
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TermsCheckbox(checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top,
    ) {
        AnimatedCheckbox(checked = checked, onToggle = { onCheck(!checked) })
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = InkTheme.colors.textMuted, fontSize = 12.sp)) { append("I agree to RiwayaApp ") }
                withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)) { append("Terms of service") }
                withStyle(SpanStyle(color = InkTheme.colors.textMuted, fontSize = 12.sp)) { append(" and ") }
                withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)) { append("Privacy policy") }
            },
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun LinkRow(text: String, linkLabel: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        ClickableText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = InkTheme.colors.textFaint, fontSize = 13.sp)) { append("$text ") }
                withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)) { append(linkLabel) }
            },
            onClick = { onClick() },
        )
    }
}