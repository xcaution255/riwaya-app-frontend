package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.excaution.riwayaapp.presentation.theme.InkTheme

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    val isLoggedIn by Session.isLoggedIn.collectAsState()

    // `remember` (not `remember` inside the effect) so this survives
    // across LaunchedEffect re-launches — it only needs to be true once,
    // for the very first composition.
    val isFirstComposition = remember { mutableStateOf(true) }

    LaunchedEffect(isLoggedIn) {
        if (isFirstComposition.value) {
            // On app start the NavHost's startDestination already matches
            // isLoggedIn — skip navigating so we don't play a pointless
            // transition animation the instant the app opens.
            isFirstComposition.value = false
            return@LaunchedEffect
        }

        val target = if (isLoggedIn) Route.MainGraph else Route.AuthGraph
        navController.navigate(target) {
            // This is what makes logout SAFE: popUpTo(0) wipes the entire
            // back stack (Home, Profile, Explore, Notifications, and any
            // auth screens) every time the auth state flips. A back-press
            // from Login can never resurface the previous session's screens,
            // and logging back in never resurfaces a stale Login screen.
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize(),color = InkTheme.colors.bgDeep) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Route.MainGraph else Route.AuthGraph
        ) {
            authGraph(navController)

            composable<Route.MainGraph> {
                MainScaffold(onLogout = { Session.logout() })
            }
        }
    }
}
