package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.excaution.riwayaapp.presentation.theme.RiwayaAppTheme

@Composable
fun App() {
    // Hold your theme variable state in a mutable state container
    var isDarkTheme by remember { mutableStateOf(false) }
    RiwayaAppTheme(darkTheme = isDarkTheme) {
        RootNavGraph()
    }
}
