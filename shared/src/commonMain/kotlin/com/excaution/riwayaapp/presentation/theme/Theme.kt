package com.excaution.riwayaapp.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val InkFlowTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp,
        color = TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.3).sp,
        color = TextPrimary,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
        color = TextPrimary,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color = TextPrimary,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color = TextSecondary,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        color = TextMuted,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
        color = TextSecondary,
    ),
)

//for light and dark theme
data class InkColorScheme(
    val bgDeep: Color, val bgSurface: Color,
    val bgCard: Color, val bgBorder: Color,
    val accentPrimary: Color, val accentLight: Color,
    val textPrimary: Color, val textSecondary: Color,
    val textMuted: Color, val textFaint: Color,
    val successGreen: Color, val dangerRed: Color,
    val starColor: Color, val genreMystery: Color, val genreSciFi: Color,
    val genreHorror: Color, val bgInput: Color, val bgFocused: Color,
    val bgValid: Color, val bgError: Color, val bgOtp: Color
)

val darkScheme = InkColorScheme(
    bgDeep = Color(0xFF0D0D14), bgSurface = Color(0xFF191924),
    bgCard = Color(0xFF1C1C28), bgBorder = Color(0xFF2A2A3E),
    accentPrimary = Color(0xFF7C6AF7), accentLight = Color(0xFFA78BFA),
    textPrimary = Color(0xFFF0EFF8), textSecondary = Color(0xFF9B9BB8),
    textMuted = Color(0xFF6A6A88), textFaint = Color(0xFF4A4A6A),
    successGreen = Color(0xFF4ADE80), dangerRed = Color(0xFFE24B4A),
    starColor = Color(0xFFF59E0B),
    genreMystery = GenreMystery,
    genreSciFi = GenreSciFi,
    genreHorror = GenreHorror,
    bgInput = BgInput,
    bgFocused = Color(0xFF16163A),
    bgValid = Color(0xFF0E1A14),
    bgError = Color(0xFF1A0E0E),
    bgOtp = Color(0xFF16163A),
)
val lightScheme = InkColorScheme(
    bgDeep = Color(0xFFEDEAFF), bgSurface = Color(0xFFFFFFFF), //bgDepp before Color(0xFFF4F2FF)
    bgCard = Color(0xFFEDEAFF), bgBorder = Color(0xFFD8D3FF),
    accentPrimary = Color(0xFF6C5CE7), accentLight = Color(0xFF8B7CF8),
    textPrimary = Color(0xFF1A1530), textSecondary = Color(0xFF4A4270),
    textMuted = Color(0xFF7A73A8), textFaint = Color(0xFFA9A4CC),
    successGreen = Color(0xFF16A34A), dangerRed = Color(0xFFDC2626),
    starColor = Color(0xFFD97706),
    genreMystery = GenreMystery,
    genreSciFi = GenreSciFi,
    genreHorror = GenreHorror,
    bgInput = Color(0xFFF4F2FF),
    bgFocused = Color(0xFFF0F2FA),
    bgValid = Color(0xFFF0F7F4),
    bgError = Color(0xFFFAF0F0),
    bgOtp = Color(0xFFF0F2FA)
)



//added
// 3. Composition Locals for dynamic state toggling across screens
val LocalInkColors = compositionLocalOf { lightScheme }
val LocalThemeIsDark = compositionLocalOf { mutableStateOf(false) }

// 4. Clean object provider to read values like "InkTheme.colors.bgDeep"
object InkTheme {
    val colors: InkColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalInkColors.current
}

// 5. The Core Theme Provider
@Composable
fun RiwayaAppTheme(
    // 1. Accept the dark theme setting explicitly from outside
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val systemDark = isSystemInDarkTheme()
//    val isDarkState = remember { mutableStateOf(systemDark) }

    // Choose appropriate custom layout properties
    val inkColors = if (darkTheme) darkScheme else lightScheme

    // Backport structural tokens directly to foundational Material3 hooks
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = darkScheme.accentPrimary,
            background = darkScheme.bgDeep,
            surface = darkScheme.bgSurface,
            onBackground = darkScheme.textPrimary,
            onSurface = darkScheme.textPrimary
        )
    } else {
        lightColorScheme(
            primary = lightScheme.accentPrimary,
            background = lightScheme.bgDeep,
            surface = lightScheme.bgSurface,
            onBackground = lightScheme.textPrimary,
            onSurface = lightScheme.textPrimary
        )
    }

    CompositionLocalProvider(
        LocalInkColors provides inkColors,
//        LocalThemeIsDark provides isDarkState
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}

// CompositionLocal to pass scheme down the tree
//val LocalInkColors = compositionLocalOf { darkScheme }
//
//// Convenience accessor — use anywhere in UI
//val inkColors: InkColorScheme
//    @Composable get() = LocalInkColors.current
//
//@Composable
//fun RiwayaAppTheme(
//    isDark: Boolean = true,         // ← single switch
//    content: @Composable () -> Unit,
//) {
//    val colors = if (isDark) darkScheme else lightScheme
//    // Animate every color change smoothly
//    val animBgDeep by animateColorAsState(colors.bgDeep, tween(400))
//    val animAccent by animateColorAsState(colors.accentPrimary, tween(400))
//    val animBgSurface by animateColorAsState(colors.bgSurface, tween(400), label = "bgSurface")
//    val animBgCard by animateColorAsState(colors.bgCard, tween(400), label = "bgCard")
//    val animBgBorder by animateColorAsState(colors.bgBorder, tween(400), label = "bgBorder")
//    val animAccentLight by animateColorAsState(colors.accentLight, tween(400), label = "accentLight")
//    val animTextPrimary by animateColorAsState(colors.textPrimary, tween(400), label = "textPrimary")
//    val animTextSecondary by animateColorAsState(colors.textSecondary, tween(400), label = "textSecondary")
//    val animTextMuted by animateColorAsState(colors.textMuted, tween(400), label = "textMuted")
//    val animTextFaint by animateColorAsState(colors.textFaint, tween(400), label = "textFaint")
//    val animSuccessGreen by animateColorAsState(colors.successGreen, tween(400), label = "successGreen")
//    val animDangerRed by animateColorAsState(colors.dangerRed, tween(400), label = "dangerRed")
//    val animStarColor by animateColorAsState(colors.starColor, tween(400), label = "starColor")
//    // ... animate all other tokens the same way ...
//    val animatedScheme = colors.copy(
//        bgDeep = animBgDeep,
//        accentPrimary = animAccent,
//        bgSurface = animBgSurface,
//        bgCard = animBgCard,
//        bgBorder = animBgBorder,
//        accentLight = animAccentLight,
//        textPrimary = animTextPrimary,
//        textSecondary = animTextSecondary,
//        textMuted = animTextMuted,
//        textFaint = animTextFaint,
//        successGreen = animSuccessGreen,
//        dangerRed = animDangerRed,
//        starColor = animStarColor
//        // ... rest of animated tokens ...
//    )
//
//    // Dynamically bridge your custom animated tokens to Material 3's structure
//    val m3ColorScheme = if (isDark) {
//        darkColorScheme(
//            primary = animatedScheme.accentPrimary,
//            onPrimary = animatedScheme.bgDeep,
//            secondary = animatedScheme.accentLight,
//            background = animatedScheme.bgDeep,
//            surface = animatedScheme.bgSurface,
//            onBackground = animatedScheme.textPrimary,
//            onSurface = animatedScheme.textPrimary,
//            error = animatedScheme.dangerRed
//        )
//    } else {
//        lightColorScheme(
//            primary = animatedScheme.accentPrimary,
//            onPrimary = animatedScheme.bgDeep,
//            secondary = animatedScheme.accentLight,
//            background = animatedScheme.bgDeep,
//            surface = animatedScheme.bgSurface,
//            onBackground = animatedScheme.textPrimary,
//            onSurface = animatedScheme.textPrimary,
//            error = animatedScheme.dangerRed
//        )
//    }
//
//    CompositionLocalProvider(LocalInkColors provides animatedScheme) {
//        MaterialTheme(
//            colorScheme = m3ColorScheme,
//            typography  = InkFlowTypography,
//            content     = content,
//        )
//    }
//}

