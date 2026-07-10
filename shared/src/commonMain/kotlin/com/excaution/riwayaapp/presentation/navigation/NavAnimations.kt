package com.excaution.riwayaapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

/**
 * Every transition here is deliberately different in *kind*, not just
 * duration/easing — because different navigation events mean different
 * things to the user, and the motion should say so:
 *
 *  - forward push within a flow      -> horizontal slide (directional)
 *  - swap between auth <-> main app  -> cross-fade + scale (no shared axis)
 *  - switching bottom-bar tabs       -> quick fade only (siblings, not a stack)
 *  - opening a modal-ish screen      -> slide up from the bottom (a sheet)
 */
private const val DURATION = 350
private const val TAB_DURATION = 220

object NavAnimations {

    val enterSlideIn: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(tween(DURATION)) { fullWidth -> fullWidth } +
            fadeIn(tween(DURATION))
    }

    val exitSlideOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(tween(DURATION)) { fullWidth -> -fullWidth / 4 } +
            fadeOut(tween(DURATION))
    }

    val popEnterSlideIn: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(tween(DURATION)) { fullWidth -> -fullWidth / 4 } +
            fadeIn(tween(DURATION))
    }

    val popExitSlideOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(tween(DURATION)) { fullWidth -> fullWidth } +
            fadeOut(tween(DURATION))
    }

    val crossFadeEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(tween(DURATION)) + scaleIn(initialScale = 0.96f, animationSpec = tween(DURATION))
    }

    val crossFadeExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(tween(DURATION)) + scaleOut(targetScale = 1.04f, animationSpec = tween(DURATION))
    }

    val tabEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(tween(TAB_DURATION))
    }

    val tabExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(tween(TAB_DURATION - 70))
    }

    val slideUpEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(tween(DURATION)) { fullHeight -> fullHeight } +
            fadeIn(tween(DURATION))
    }

    val slideDownExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(tween(DURATION)) { fullHeight -> fullHeight } +
            fadeOut(tween(DURATION))
    }
}
