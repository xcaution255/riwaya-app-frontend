package com.excaution.riwayaapp.domain.model

import androidx.compose.ui.graphics.Color
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.GenreFantasy
import com.excaution.riwayaapp.presentation.theme.GenreHorror
import com.excaution.riwayaapp.presentation.theme.GenreMystery
import com.excaution.riwayaapp.presentation.theme.GenreRomance
import com.excaution.riwayaapp.presentation.theme.GenreSciFi
import com.excaution.riwayaapp.presentation.theme.GradientFantasy
import com.excaution.riwayaapp.presentation.theme.GradientHorror
import com.excaution.riwayaapp.presentation.theme.GradientMystery
import com.excaution.riwayaapp.presentation.theme.GradientRomance
import com.excaution.riwayaapp.presentation.theme.GradientSciFi


// ── Story ─────────────────────────────────────────────────────────────────────

data class Story(
    val id: Int,
    val title: String,
    val author: String,
    val authorInitial: String,
    val genre: StoryGenre,
    val readTimeMin: Int,
    val likes: String,
    val comments: String,
    val reads: String,
    val isFeatured: Boolean = false,
    val coverGradient: List<Color>,
    val coverEmoji: String,
)

enum class StoryGenre(val label: String, val color: Color) {
    ALL      ("All", AccentPrimary),
    FANTASY  ("Fantasy", GenreFantasy),
    ROMANCE  ("Romance", GenreRomance),
    MYSTERY  ("Mystery", GenreMystery),
    SCIFI    ("Sci-Fi", GenreSciFi),
    HORROR   ("Horror", GenreHorror),
    ADVENTURE("Adventure",Color(0xFFF472B6)),
}

// ── Book ──────────────────────────────────────────────────────────────────────

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: StoryGenre,
    val price: Double,
    val originalPrice: Double?,
    val rating: Float,
    val reviewCount: String,
    val coverGradient: List<Color>,
    val coverEmoji: String,
    val isBestseller: Boolean = false,
    val isWishlisted: Boolean = false,
) {
    val discountPercent: Int?
        get() = originalPrice?.let { orig ->
            ((1.0 - price / orig) * 100).toInt().takeIf { it > 0 }
        }
}

// ── Sample Data ───────────────────────────────────────────────────────────────

object SampleData {

    val stories = listOf(
        Story(
            id = 1, title = "The Last Starweaver", author = "Elara Voss",
            authorInitial = "E", genre = StoryGenre.FANTASY,
            readTimeMin = 87, likes = "24.1k", comments = "1.8k", reads = "142k",
            isFeatured = true,
            coverGradient = GradientFantasy, coverEmoji = "✦",
        ),
        Story(
            id = 2, title = "Garden of Quiet Storms", author = "Mira Chen",
            authorInitial = "M", genre = StoryGenre.ROMANCE,
            readTimeMin = 24, likes = "24.1k", comments = "1.8k", reads = "89k",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Story(
            id = 3, title = "The Gilded Cipher", author = "Ade Okonkwo",
            authorInitial = "A", genre = StoryGenre.MYSTERY,
            readTimeMin = 41, likes = "89.4k", comments = "6.2k", reads = "201k",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Story(
            id = 4, title = "Echoes Beyond Orion", author = "Sol Martinez",
            authorInitial = "S", genre = StoryGenre.SCIFI,
            readTimeMin = 62, likes = "201k", comments = "14k", reads = "580k",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Story(
            id = 5, title = "What Sleeps Below", author = "K. Darkmore",
            authorInitial = "K", genre = StoryGenre.HORROR,
            readTimeMin = 33, likes = "54k", comments = "9.1k", reads = "120k",
            coverGradient = GradientHorror, coverEmoji = "💀",
        ),
        Story(
            id = 6, title = "The Last Starweaver", author = "Elara Voss",
            authorInitial = "E", genre = StoryGenre.FANTASY,
            readTimeMin = 87, likes = "24.1k", comments = "1.8k", reads = "142k",
            isFeatured = true,
            coverGradient = GradientFantasy, coverEmoji = "✦",
        ),
        Story(
            id = 7, title = "Garden of Quiet Storms", author = "Mira Chen",
            authorInitial = "M", genre = StoryGenre.ROMANCE,
            readTimeMin = 24, likes = "24.1k", comments = "1.8k", reads = "89k",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Story(
            id = 8, title = "The Gilded Cipher", author = "Ade Okonkwo",
            authorInitial = "A", genre = StoryGenre.MYSTERY,
            readTimeMin = 41, likes = "89.4k", comments = "6.2k", reads = "201k",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Story(
            id = 9, title = "Echoes Beyond Orion", author = "Sol Martinez",
            authorInitial = "S", genre = StoryGenre.SCIFI,
            readTimeMin = 62, likes = "201k", comments = "14k", reads = "580k",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Story(
            id = 10, title = "What Sleeps Below", author = "K. Darkmore",
            authorInitial = "K", genre = StoryGenre.HORROR,
            readTimeMin = 33, likes = "54k", comments = "9.1k", reads = "120k",
            coverGradient = GradientHorror, coverEmoji = "💀",
        )
    )

    val books = listOf(
        Book(
            id = 1, title = "Echoes of the Void", author = "Elena Vasquez",
            genre = StoryGenre.FANTASY, price = 8.99, originalPrice = 14.99,
            rating = 4.4f, reviewCount = "4,821",
            coverGradient = GradientFantasy, coverEmoji = "✦", isBestseller = true,
        ),
        Book(
            id = 2, title = "The Bloom Between Us", author = "Mira Chen",
            genre = StoryGenre.ROMANCE, price = 6.99, originalPrice = null,
            rating = 4.0f, reviewCount = "2,104",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Book(
            id = 3, title = "What Sleeps Beneath", author = "K. Darkmore",
            genre = StoryGenre.HORROR, price = 9.49, originalPrice = null,
            rating = 5.0f, reviewCount = "8,312",
            coverGradient = GradientHorror, coverEmoji = "💀", isWishlisted = true,
        ),
        Book(
            id = 4, title = "Starfall Protocol", author = "Sol Martinez",
            genre = StoryGenre.SCIFI, price = 7.99, originalPrice = 11.99,
            rating = 4.0f, reviewCount = "3,678",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Book(
            id = 5, title = "The Amber Dossier", author = "Ade Okonkwo",
            genre = StoryGenre.MYSTERY, price = 5.99, originalPrice = null,
            rating = 5.0f, reviewCount = "6,490",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Book(
            id = 6, title = "Echoes of the Void", author = "Elena Vasquez",
            genre = StoryGenre.FANTASY, price = 8.99, originalPrice = 14.99,
            rating = 4.4f, reviewCount = "4,821",
            coverGradient = GradientFantasy, coverEmoji = "✦", isBestseller = true,
        ),
        Book(
            id = 7, title = "The Bloom Between Us", author = "Mira Chen",
            genre = StoryGenre.ROMANCE, price = 6.99, originalPrice = null,
            rating = 4.0f, reviewCount = "2,104",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Book(
            id = 8, title = "What Sleeps Beneath", author = "K. Darkmore",
            genre = StoryGenre.HORROR, price = 9.49, originalPrice = null,
            rating = 5.0f, reviewCount = "8,312",
            coverGradient = GradientHorror, coverEmoji = "💀", isWishlisted = true,
        ),
        Book(
            id = 9, title = "Starfall Protocol", author = "Sol Martinez",
            genre = StoryGenre.SCIFI, price = 7.99, originalPrice = 11.99,
            rating = 4.0f, reviewCount = "3,678",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Book(
            id = 10, title = "The Amber Dossier", author = "Ade Okonkwo",
            genre = StoryGenre.MYSTERY, price = 5.99, originalPrice = null,
            rating = 5.0f, reviewCount = "6,490",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
    )
}
