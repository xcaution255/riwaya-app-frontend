package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.excaution.riwayaapp.presentation.navigation.bottombar.FloatingPillNavBar
import com.excaution.riwayaapp.data.auth.AuthRepository
import com.excaution.riwayaapp.presentation.books.BookStoreScreen
import com.excaution.riwayaapp.presentation.home.HomeScreen
import com.excaution.riwayaapp.presentation.navigation.bottombar.BottomViewModel
import com.excaution.riwayaapp.presentation.notifications.NotificationScreen
import com.excaution.riwayaapp.presentation.profile.ProfileScreen
import com.excaution.riwayaapp.presentation.saved.SavedScreen
import com.excaution.riwayaapp.presentation.search.SearchScreen
import com.excaution.riwayaapp.presentation.theme.InkTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val authRepository: AuthRepository = koinInject()

    // Tracks total bar height dynamically (including system padding)
    var bottomBarHeightPx by remember { mutableStateOf(0f) }
    // Tracks translation Y offset
    var bottomBarOffsetHeightPx by remember { mutableStateOf(0f) }
    // Intercept scroll deltas from any child scrollable list
    val nestedScrollConnection = remember(bottomBarHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx + delta

                // Keep offset restricted between 0 (visible) and negative height (hidden)
                if (bottomBarHeightPx > 0f) {
                    bottomBarOffsetHeightPx = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                }

                return Offset.Zero
            }
        }
    }
    
    Scaffold(
        containerColor = InkTheme.colors.bgDeep,
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)) {
            NavHost(
                navController = navController,
                startDestination = Route.Main.Home,
                modifier = Modifier.padding(innerPadding),
                enterTransition = NavAnimations.tabEnter,
                exitTransition = NavAnimations.tabExit,
            ) {
                composable<Route.Main.Home>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { HomeScreen(onNotificationClick = {navController.navigate(Route.Main.Notifications)})}

                composable<Route.Main.BookShop>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { BookStoreScreen() }

                composable<Route.Main.Profile>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { ProfileScreen(onLogout = {authRepository.logout()})}

                composable<Route.Main.Notifications>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { NotificationScreen(onBack = {navController.navigate(Route.Main.Home)}) }

                composable<Route.Main.Saved>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { SavedScreen() }

                composable<Route.Main.Search>(
                    enterTransition = NavAnimations.slideUpEnter,
                    exitTransition = NavAnimations.slideDownExit,
                    popEnterTransition = NavAnimations.slideUpEnter,
                    popExitTransition = NavAnimations.slideDownExit,
                ) { SearchScreen() }
            }

            FloatingPillNavBar(
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it },
                onSearchClick = { navController.navigate(Route.Main.Search) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { coordinates ->
                        bottomBarHeightPx = coordinates.size.height.toFloat()
                    }
                    .graphicsLayer {
                        // Pulls the bar downwards relative to its height bounds
                        translationY = -bottomBarOffsetHeightPx
                    },
                onHomeClick = {navController.navigate(Route.Main.Home) },
                onBooksClick = {navController.navigate(Route.Main.BookShop)},
                onSavedClick = {navController.navigate(Route.Main.Saved)},
                onProfileClick = {navController.navigate(Route.Main.Profile)}
            )
        }
    }
}