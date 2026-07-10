package com.excaution.riwayaapp.presentation.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.domain.model.SampleData
import com.excaution.riwayaapp.domain.model.StoryGenre
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.InkTheme


@Composable
fun SearchScreen() {
    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Stable list state — critical for smooth scroll
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {SearchBar()}
    ) {}
}
