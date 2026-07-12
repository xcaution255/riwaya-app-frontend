package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.excaution.riwayaapp.presentation.books.BookStoreScreen
import com.excaution.riwayaapp.presentation.home.HomeScreen
import com.excaution.riwayaapp.presentation.navigation.bottombar.BottomViewModel
import com.excaution.riwayaapp.presentation.navigation.bottombar.FloatingBottomBar
import com.excaution.riwayaapp.presentation.notifications.NotificationScreen
import com.excaution.riwayaapp.presentation.profile.ProfileScreen
import com.excaution.riwayaapp.presentation.saved.SavedScreen
import com.excaution.riwayaapp.presentation.search.SearchScreen
import com.excaution.riwayaapp.presentation.theme.InkTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScaffold(
    onLogout: () -> Unit
) {
    val bottomVM : BottomViewModel = koinViewModel()
    val bottomState by bottomVM.uiState.collectAsState()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    
    Scaffold(
        containerColor = InkTheme.colors.bgDeep,
        bottomBar = {
            FloatingBottomBar(
                selectedIndex = bottomState.selectedBottomIndex,
                onItemSelected = bottomVM::selectBottom,
                onSearchClick = {navController.navigate(Route.Main.Search)},
                modifier = Modifier,
                onHomeClick = {navController.navigate(Route.Main.Home) },
                onBookShopClick = {navController.navigate(Route.Main.BookShop)},
                onSavedClick = {navController.navigate(Route.Main.Saved)},
                onProfileClick = {navController.navigate(Route.Main.Profile)}
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Main.Home,
            modifier = Modifier.padding(innerPadding),
            enterTransition = NavAnimations.tabEnter,
            exitTransition = NavAnimations.tabExit,
        ) {
            composable<Route.Main.Home> { HomeScreen(
                onNotificationClick = {navController.navigate(Route.Main.Notifications)}
            )}
            composable<Route.Main.BookShop> { BookStoreScreen() }
            composable<Route.Main.Profile> { ProfileScreen(onLogout = onLogout)}
            composable<Route.Main.Saved> { }

            composable<Route.Main.Notifications>(
                enterTransition = NavAnimations.slideUpEnter,
                exitTransition = NavAnimations.slideDownExit,
                popEnterTransition = NavAnimations.slideUpEnter,
                popExitTransition = NavAnimations.slideDownExit,
            ) {
                NotificationScreen()
            }

            composable<Route.Main.Saved>(
                enterTransition = NavAnimations.slideUpEnter,
                exitTransition = NavAnimations.slideDownExit,
                popEnterTransition = NavAnimations.slideUpEnter,
                popExitTransition = NavAnimations.slideDownExit,
            ) {
                SavedScreen()
            }

            composable<Route.Main.Search>(
                enterTransition = NavAnimations.slideUpEnter,
                exitTransition = NavAnimations.slideDownExit,
                popEnterTransition = NavAnimations.slideUpEnter,
                popExitTransition = NavAnimations.slideDownExit,
            ) {
                SearchScreen()
            }
        }
    }
}