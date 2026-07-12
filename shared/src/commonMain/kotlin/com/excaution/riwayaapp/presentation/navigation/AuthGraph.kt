package com.excaution.riwayaapp.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.excaution.riwayaapp.presentation.auth.emailverify.VerifyEmailScreen
import com.excaution.riwayaapp.presentation.auth.login.LoginScreen
import com.excaution.riwayaapp.presentation.auth.otp.OtpVerifyScreen
import com.excaution.riwayaapp.presentation.auth.passwordrecovery.ForgotPasswordScreen
import com.excaution.riwayaapp.presentation.auth.passwordrecovery.PasswordRecoveryScreen
import com.excaution.riwayaapp.presentation.auth.register.RegisterScreen
import com.excaution.riwayaapp.presentation.auth.splash.SplashScreen

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Route.AuthGraph>(startDestination = Route.Auth.Splash) {

        composable<Route.Auth.Splash>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            SplashScreen(
                onFinished = {navController.navigate(Route.Auth.Login) {
                    popUpTo(Route.Auth.Splash) { inclusive = true }
                } }
            )
        }

//        composable<Route.Auth.Onboarding>(
////            enterTransition = NavAnimations.enterSlideIn,
////            exitTransition = NavAnimations.exitSlideOut,
////            popEnterTransition = NavAnimations.popEnterSlideIn,
////            popExitTransition = NavAnimations.popExitSlideOut,
//        ) {
//            OnboardingScreen(
//                onFinished = {navController.navigate(Route.Auth.Register) {
//                    popUpTo(Route.Auth.Onboarding) { inclusive = true }
//                } }
//            )
//        }

        composable<Route.Auth.Login>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            LoginScreen(
                // Session.login() flips the single source of truth;
                // RootNavGraph reacts to it and swaps graphs safely.
                onLoginSuccess = { Session.login() },
                onNavigateToRegister = { navController.navigate(Route.Auth.Register) },
                onNavigateToForgotPassword = { navController.navigate(Route.Auth.ForgotPassword) },
            )
        }

        composable<Route.Auth.Register>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            RegisterScreen(
                onRegisterSuccess = {Session.login()},
                onNavigateToLogin = {navController.navigate(Route.Auth.Login)},
            )
        }

        composable<Route.Auth.ForgotPassword>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            ForgotPasswordScreen(
                onBack = {navController.navigate(Route.Auth.Login)},
                onSendLink = {navController.navigate(Route.Auth.OtpVerify)},
                onSendOtp = {navController.navigate(Route.Auth.Login)},
            )
        }

        composable<Route.Auth.OtpVerify>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            OtpVerifyScreen(
                onBack = { navController.navigate(Route.Auth.Login) },
                emailHint = "aug...com",
                onVerified = { navController.navigate(Route.Auth.PasswordRecoveryScreen) }
            )
        }

        composable<Route.Auth.VerifyEmail>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            VerifyEmailScreen(
                email = "Augustinow206@gmail.com",
                onBack = {navController.navigate(Route.Auth.Login)},
                onEnterOtp = {navController.navigate(Route.Auth.OtpVerify)},
                onResend = {}
            )
        }

        composable<Route.Auth.PasswordRecoveryScreen>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            PasswordRecoveryScreen(
                onBack = {navController.navigate(Route.Auth.OtpVerify)},
                onPasswordSet = {navController.navigate(Route.Auth.Login)}
            )
        }
    }
}
