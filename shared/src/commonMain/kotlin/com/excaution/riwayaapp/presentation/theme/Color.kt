package com.excaution.riwayaapp.presentation.theme

import androidx.compose.ui.graphics.Color

// ── Base Background ──────────────────────────────────────────────────────────
val BgDeep        = Color(0xFF0D0D14)
val BgSurface     = Color(0xFF191924)
val BgCard        = Color(0xFF1C1C28)
val BgBorder      = Color(0xFF2A2A3E)
val BgInput       = Color(0xFF16161E)

// ── Accent ───────────────────────────────────────────────────────────────────
val AccentPrimary   = Color(0xFF7C6AF7)
val AccentLight     = Color(0xFFA78BFA)
val AccentGlow      = Color(0x267C6AF7)   // 15% alpha

// ── Text ─────────────────────────────────────────────────────────────────────
val TextPrimary = Color(0xFFF0EFF8)
val TextSecondary = Color(0xFF9B9BB8)
val TextMuted = Color(0xFF6A6A88)
val TextFaint = Color(0xFF4A4A6A)

// ── Genre Colors ─────────────────────────────────────────────────────────────
val GenreFantasy = Color(0xFFA78BFA)
val GenreRomance = Color(0xFF4ADE80)
val GenreMystery = Color(0xFFEF9F27)
val GenreSciFi = Color(0xFF60A5FA)
val GenreHorror = Color(0xFFE24B4A)

// ── Utility ──────────────────────────────────────────────────────────────────
val StarColor = Color(0xFFF59E0B)
val SuccessGreen = Color(0xFF4ADE80)
val DangerRed = Color(0xFFE24B4A)

// ── Gradients (use with Brush.linearGradient) ────────────────────────────────
val GradientAccent  = listOf(AccentPrimary, AccentLight)
val GradientFeatured= listOf(Color(0xFF1A0D3E), Color(0xFF0D1A3E))
val GradientRomance = listOf(Color(0xFF0F2A1E), Color(0xFF1F6A4A))
val GradientMystery = listOf(Color(0xFF3E2A0F), Color(0xFF7A4A1F))
val GradientSciFi   = listOf(Color(0xFF0F1E3E), Color(0xFF1F4A7A))
val GradientHorror  = listOf(Color(0xFF1A0A0A), Color(0xFF4A1010))
val GradientFantasy = listOf(Color(0xFF1A0A3E), Color(0xFF2E1260))


//for light and dark theme
object InkDarkColors {
    val BgDeep       = Color(0xFF0D0D14)
    val BgSurface    = Color(0xFF191924)
    val BgCard       = Color(0xFF1C1C28)
    val BgBorder     = Color(0xFF2A2A3E)
    val AccentPrimary = Color(0xFF7C6AF7)
    val AccentLight   = Color(0xFFA78BFA)
    val TextPrimary   = Color(0xFFF0EFF8)
    val TextSecondary = Color(0xFF9B9BB8)
    val TextMuted     = Color(0xFF6A6A88)
    val TextFaint     = Color(0xFF4A4A6A)
}

object InkLightColors {
    val BgDeep       = Color(0xFFF4F2FF)
    val BgSurface    = Color(0xFFFFFFFF)
    val BgCard       = Color(0xFFEDEAFF)
    val BgBorder     = Color(0xFFD8D3FF)
    val AccentPrimary = Color(0xFF6C5CE7)
    val AccentLight   = Color(0xFF8B7CF8)
    val TextPrimary   = Color(0xFF1A1530)
    val TextSecondary = Color(0xFF4A4270)
    val TextMuted     = Color(0xFF7A73A8)
    val TextFaint     = Color(0xFFA9A4CC)
}