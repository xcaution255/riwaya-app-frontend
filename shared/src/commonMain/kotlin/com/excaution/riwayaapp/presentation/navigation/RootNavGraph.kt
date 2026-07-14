package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.excaution.riwayaapp.data.auth.AuthRepository
import com.excaution.riwayaapp.presentation.theme.InkTheme
import org.koin.compose.koinInject

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    val authRepository: AuthRepository = koinInject()
    val isLoggedIn by authRepository.isLoggedIn.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(),color = InkTheme.colors.bgDeep) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Route.MainGraph else Route.AuthGraph
        ) {
            authGraph(navController)

            composable<Route.MainGraph> {
                MainScreen()
            }
        }
    }
}
