package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.excaution.riwayaapp.presentation.theme.LocalThemeController
import com.excaution.riwayaapp.presentation.theme.RiwayaAppTheme

@Composable
fun App() {
    val isDarkTheme = remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalThemeController provides isDarkTheme) {

        RiwayaAppTheme(darkTheme = isDarkTheme.value) {
            RootNavGraph()
        }
    }
} //should not be able to take screenshort
