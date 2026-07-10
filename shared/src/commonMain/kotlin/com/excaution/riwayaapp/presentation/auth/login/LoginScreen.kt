package com.excaution.riwayaapp.presentation.auth.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.excaution.riwayaapp.presentation.components.AuthBrandHeader
import com.excaution.riwayaapp.presentation.components.AuthDivider
import com.excaution.riwayaapp.presentation.components.AuthField
import com.excaution.riwayaapp.presentation.components.AuthFieldState
import com.excaution.riwayaapp.presentation.components.AuthHeader
import com.excaution.riwayaapp.presentation.components.AuthPrimaryButton
import com.excaution.riwayaapp.presentation.components.AuthScaffold
import com.excaution.riwayaapp.presentation.components.ErrorBanner
import com.excaution.riwayaapp.presentation.components.SocialButtonsRow
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.TextFaint


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val emailState = when {
        email.isEmpty()     -> AuthFieldState.IDLE
        email.contains("@") -> AuthFieldState.VALID
        else                -> AuthFieldState.ERROR
    }
    val passwordState = if (showError) AuthFieldState.ERROR else AuthFieldState.IDLE

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AuthScaffold {
//        AnimatedVisibility(visible = visible, enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -10 }) {
//            AuthBrandHeader()
//        }
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(320, 50)) + slideInVertically(tween(320, 50)) { 20 }) {
            AuthHeader(
                tag      = "",
                title    = "Sign in",
                subtitle = "Good to see you again",
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(340, 80)) + slideInVertically(tween(340, 80)) { 20 }) {
                AuthField(
                    label         = "Email address",
                    value         = email,
                    onValueChange = { email = it; showError = false },
                    placeholder   = "you@example.com",
                    state         = emailState,
                    keyboardType  = KeyboardType.Email,
                    trailingIcon  = Icons.Rounded.AlternateEmail,
                    imeAction     = ImeAction.Next,
                )
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(355, 110)) + slideInVertically(tween(355, 110)) { 20 }) {
                AuthField(
                    label         = "Password",
                    value         = password,
                    onValueChange = { password = it; showError = false },
                    placeholder   = "Your password",
                    state         = passwordState,
                    isPassword    = true,
                    keyboardType  = KeyboardType.Password,
                    imeAction     = ImeAction.Done,
                    onImeAction   = { showError = password.isEmpty() },
                )
            }

            // Animated error banner
            AnimatedVisibility(
                visible = showError,
                enter   = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit    = fadeOut(tween(200)) + shrinkVertically(tween(200)),
            ) {
                ErrorBanner("Incorrect email or password. Please try again.")
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(370, 140))) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = AccentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)) {
                                append("Forgot password?")
                            }
                        },
                        onClick = { onNavigateToForgotPassword() },
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(380, 170))) {
                AuthPrimaryButton(
                    text    = "Sign in",
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            showError = true
                        } else {
                            isLoading = true
                            onLoginSuccess()
                        }
                    },
                    icon      = Icons.Rounded.Login,
                    isLoading = isLoading,
                )
            }
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(410, 230))) {
                Box(contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = InkTheme.colors.textFaint, fontSize = 13.sp)) { append("Don't have an account? ") }
                            withStyle(SpanStyle(color = InkTheme.colors.accentPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)) { append("Create one") }
                        },
                        onClick = { onNavigateToRegister() },
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
