package com.excaution.riwayaapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
    val bgValid: Color, val bgError: Color, val bgOtp: Color,
    val coverGradient: List<Color>
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
    coverGradient = listOf(Color(0xFF1A0D3E), Color(0xFF0D1A3E)),
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
    bgOtp = Color(0xFFF0F2FA),
    coverGradient = listOf(Color(0xFFE8E1F7), Color(0xFFE1EDF7))
)



//added
// 3. Composition Locals for dynamic state toggling across screens
val LocalInkColors = compositionLocalOf { lightScheme }

// 4. Clean object provider to read values like "InkTheme.colors.bgDeep"
object InkTheme {
    val colors: InkColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalInkColors.current
}

// This provides a safe fallback structure if accessed outside the theme wrapper
val LocalThemeController = compositionLocalOf<MutableState<Boolean>> {
    error("No Theme Controller provided")
}

@Composable
fun RiwayaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val inkColors = if (darkTheme) darkScheme else lightScheme

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
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}