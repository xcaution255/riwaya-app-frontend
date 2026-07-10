package com.excaution.riwayaapp.presentation.auth.onboarding

import androidx.compose.ui.graphics.Color
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.GenreSciFi
import com.excaution.riwayaapp.presentation.theme.SuccessGreen


// ── Page descriptor ───────────────────────────────────────────────────────────

data class OnboardingPage(
    val index: Int,
    val tag: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val ctaLabel: String,
)

// ── All three pages ───────────────────────────────────────────────────────────

val onboardingPages = listOf(
    OnboardingPage(
        index       = 0,
        tag         = "WRITE",
        title       = "Your stories deserve\nto be heard",
        description = "Write, edit, and publish in a distraction‑free editor built for storytellers. Your words, your world.",
        accentColor = AccentPrimary,
        ctaLabel    = "Continue",
    ),
    OnboardingPage(
        index       = 1,
        tag         = "DISCOVER",
        title       = "Millions of stories\nwaiting for you",
        description = "Explore tales across every genre, follow your favourite authors, and never run out of something great to read.",
        accentColor = GenreSciFi,
        ctaLabel    = "Continue",
    ),
    OnboardingPage(
        index       = 2,
        tag         = "EARN",
        title       = "Turn your passion\ninto income",
        description = "Sell your books, earn from reads, and build a loyal fanbase. Your creativity is worth more than you think.",
        accentColor = SuccessGreen,
        ctaLabel    = "Get started",
    ),
)
