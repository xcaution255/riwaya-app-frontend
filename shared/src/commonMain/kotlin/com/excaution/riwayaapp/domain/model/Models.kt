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

enum class StoryGenreFeed(val label: String, val color: Color, val bgAlpha: Float) {
    ALL      ("All", AccentPrimary, 0.12f),
    STORIES  ("Stories",   Color(0xFFA78BFA), 0.12f),
    ENTERTAINMENT  ("Entertainment",   Color(0xFF4ADE80), 0.10f),
    ARTICLES  ("Articles",   Color(0xFFEF9F27), 0.10f),
    SPORTS    ("Sports",    Color(0xFF60A5FA), 0.12f),
    MOVIES   ("Movies",    Color(0xFFE24B4A), 0.10f),
}

data class StoryFeedItem(
    val id: String,
    val title: String,
    val authorName: String,
    val authorInitial: String,
    val authorGradient: List<Color>,
    val isAuthor: Boolean = false,
    val genre: StoryGenreFeed,
    val previewBody: String,       // short excerpt shown collapsed
    val fullBody: String,          // entire story text shown expanded
    val readTimeMin: Int,
    val chapterLabel: String,      // e.g. "Ch. 11 of ∞"
    val views: String,
    val likeCount: Long,
    val commentCount: String,
    val rating: Float,
    val isLive: Boolean = false,
    val hasVideo: Boolean = false,
    val isFeatured: Boolean = false, //i will remove it
    val timeAgo: String? = null,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
)

// Story

data class Story(
    val id: Int,
    val title: String,
    val author: String,
    val authorInitial: String,
    val genre: StoryGenreFeed,
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

// Book

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: StoryGenreFeed,
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

// Sample Data

object SampleData {

    val stories = listOf(
        Story(
            id = 1, title = "The Last Starweaver", author = "Elara Voss",
            authorInitial = "E", genre = StoryGenreFeed.STORIES,
            readTimeMin = 87, likes = "24.1k", comments = "1.8k", reads = "142k",
            isFeatured = true,
            coverGradient = GradientFantasy, coverEmoji = "✦",
        ),
        Story(
            id = 2, title = "Garden of Quiet Storms", author = "Mira Chen",
            authorInitial = "M", genre = StoryGenreFeed.ENTERTAINMENT,
            readTimeMin = 24, likes = "24.1k", comments = "1.8k", reads = "89k",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Story(
            id = 3, title = "The Gilded Cipher", author = "Ade Okonkwo",
            authorInitial = "A", genre = StoryGenreFeed.ARTICLES,
            readTimeMin = 41, likes = "89.4k", comments = "6.2k", reads = "201k",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Story(
            id = 4, title = "Echoes Beyond Orion", author = "Sol Martinez",
            authorInitial = "S", genre = StoryGenreFeed.SPORTS,
            readTimeMin = 62, likes = "201k", comments = "14k", reads = "580k",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Story(
            id = 5, title = "What Sleeps Below", author = "K. Darkmore",
            authorInitial = "K", genre = StoryGenreFeed.MOVIES,
            readTimeMin = 33, likes = "54k", comments = "9.1k", reads = "120k",
            coverGradient = GradientHorror, coverEmoji = "💀",
        ),
        Story(
            id = 6, title = "The Last Starweaver", author = "Elara Voss",
            authorInitial = "E", genre = StoryGenreFeed.STORIES,
            readTimeMin = 87, likes = "24.1k", comments = "1.8k", reads = "142k",
            isFeatured = true,
            coverGradient = GradientFantasy, coverEmoji = "✦",
        ),
        Story(
            id = 7, title = "Garden of Quiet Storms", author = "Mira Chen",
            authorInitial = "M", genre = StoryGenreFeed.ENTERTAINMENT,
            readTimeMin = 24, likes = "24.1k", comments = "1.8k", reads = "89k",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Story(
            id = 8, title = "The Gilded Cipher", author = "Ade Okonkwo",
            authorInitial = "A", genre = StoryGenreFeed.ARTICLES,
            readTimeMin = 41, likes = "89.4k", comments = "6.2k", reads = "201k",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Story(
            id = 9, title = "Echoes Beyond Orion", author = "Sol Martinez",
            authorInitial = "S", genre = StoryGenreFeed.SPORTS,
            readTimeMin = 62, likes = "201k", comments = "14k", reads = "580k",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Story(
            id = 10, title = "What Sleeps Below", author = "K. Darkmore",
            authorInitial = "K", genre = StoryGenreFeed.MOVIES,
            readTimeMin = 33, likes = "54k", comments = "9.1k", reads = "120k",
            coverGradient = GradientHorror, coverEmoji = "💀",
        )
    )

    val books = listOf(
        Book(
            id = 1, title = "Echoes of the Void", author = "Elena Vasquez",
            genre = StoryGenreFeed.STORIES, price = 8.99, originalPrice = 14.99,
            rating = 4.4f, reviewCount = "4,821",
            coverGradient = GradientFantasy, coverEmoji = "✦", isBestseller = true,
        ),
        Book(
            id = 2, title = "The Bloom Between Us", author = "Mira Chen",
            genre = StoryGenreFeed.ENTERTAINMENT, price = 6.99, originalPrice = null,
            rating = 4.0f, reviewCount = "2,104",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Book(
            id = 3, title = "What Sleeps Beneath", author = "K. Darkmore",
            genre = StoryGenreFeed.MOVIES, price = 9.49, originalPrice = null,
            rating = 5.0f, reviewCount = "8,312",
            coverGradient = GradientHorror, coverEmoji = "💀", isWishlisted = true,
        ),
        Book(
            id = 4, title = "Starfall Protocol", author = "Sol Martinez",
            genre = StoryGenreFeed.SPORTS, price = 7.99, originalPrice = 11.99,
            rating = 4.0f, reviewCount = "3,678",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Book(
            id = 5, title = "The Amber Dossier", author = "Ade Okonkwo",
            genre = StoryGenreFeed.ARTICLES, price = 5.99, originalPrice = null,
            rating = 5.0f, reviewCount = "6,490",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
        Book(
            id = 6, title = "Echoes of the Void", author = "Elena Vasquez",
            genre = StoryGenreFeed.STORIES, price = 8.99, originalPrice = 14.99,
            rating = 4.4f, reviewCount = "4,821",
            coverGradient = GradientFantasy, coverEmoji = "✦", isBestseller = true,
        ),
        Book(
            id = 7, title = "The Bloom Between Us", author = "Mira Chen",
            genre = StoryGenreFeed.ENTERTAINMENT, price = 6.99, originalPrice = null,
            rating = 4.0f, reviewCount = "2,104",
            coverGradient = GradientRomance, coverEmoji = "🌿",
        ),
        Book(
            id = 8, title = "What Sleeps Beneath", author = "K. Darkmore",
            genre = StoryGenreFeed.MOVIES, price = 9.49, originalPrice = null,
            rating = 5.0f, reviewCount = "8,312",
            coverGradient = GradientHorror, coverEmoji = "💀", isWishlisted = true,
        ),
        Book(
            id = 9, title = "Starfall Protocol", author = "Sol Martinez",
            genre = StoryGenreFeed.SPORTS, price = 7.99, originalPrice = 11.99,
            rating = 4.0f, reviewCount = "3,678",
            coverGradient = GradientSciFi, coverEmoji = "🚀",
        ),
        Book(
            id = 10, title = "The Amber Dossier", author = "Ade Okonkwo",
            genre = StoryGenreFeed.ARTICLES, price = 5.99, originalPrice = null,
            rating = 5.0f, reviewCount = "6,490",
            coverGradient = GradientMystery, coverEmoji = "🔍",
        ),
    )

    val storyFeed = listOf(
        StoryFeedItem(
        id = "1",
        title = "The Last KingdThis is the complete story text that will be shown when expandedom",
        authorName = "Tino",
        authorInitial = "J",
        authorGradient = listOf(
            Color(0xFF7C4DFF),
            Color(0xFF448AFF)
        ),
        genre = StoryGenreFeed.STORIES,
        previewBody = "This is a short preview of the story...",
        fullBody = "This is the complete This is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedstory text that will be shown when expanded.",
        readTimeMin = 12,
        chapterLabel = "Ch. 1",
        views = "12.5K",
        likeCount = 245,
        commentCount = "34",
        rating = 4.8f,
        isLive = true,
        hasVideo = false,
        timeAgo = "2h ago"
    ),

        StoryFeedItem(
            id = "2",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short prThis is the complete story text that will be shown when expandedeview of the story...",
            fullBody = "This is the complete sThis is the complete story text that will be shown when expandedtory text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "3",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ARTICLES,
            previewBody = "This is a This is the complete story text that will be shown when expandedshort preview of the story...",
            fullBody = "This is the completeThis is theThis is the complete story text that will be shown when expanded complete story text that will be shown when expandedThis is the complete story text that will be shown when expanded story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "4",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the comThis is the complete story text that will be shown when expandedplete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "5",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ARTICLES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "6",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a This is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedshort preview of the story...",
            fullBody = "This is the complete story This is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedThis is the complete story text that will be shown when expandedtext that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "8",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ARTICLES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "9",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "10",
            title = "The Last Kingdom",
            authorName = "Emmavice",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "11",
            title = "The Last Kingdom",
            authorName = "Vice",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ARTICLES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "12",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "13",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.MOVIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "14",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.MOVIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "15",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ENTERTAINMENT,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "16",
            title = "The Last Kingdom",
            authorName = "John Doe",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.ENTERTAINMENT,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
        StoryFeedItem(
            id = "17",
            title = "The Last Kingdom",
            authorName = "Tino",
            authorInitial = "J",
            authorGradient = listOf(
                Color(0xFF7C4DFF),
                Color(0xFF448AFF)
            ),
            genre = StoryGenreFeed.STORIES,
            previewBody = "This is a short preview of the story...",
            fullBody = "This is the complete story text that will be shown when expanded.",
            readTimeMin = 12,
            chapterLabel = "Ch. 1",
            views = "12.5K",
            likeCount = 245,
            commentCount = "34",
            rating = 4.8f,
            isLive = true,
            hasVideo = false,
            timeAgo = "2h ago"
        ),
    )
}
