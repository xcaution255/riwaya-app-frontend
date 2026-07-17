package com.excaution.riwayaapp.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
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
                onFinished = {navController.navigate(Route.Auth.Login()) {
                    popUpTo(Route.Auth.Splash) { inclusive = true }
                } }
            )
        }


        composable<Route.Auth.Login>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) { backStackEntry ->
            // Extract the type-safe arguments from the route instance
            val loginRoute = backStackEntry.toRoute<Route.Auth.Login>()

            LoginScreen(
                // Pass the extracted email down to your login UI / ViewModel state
                initialEmail = loginRoute.email ?: "",
                onLoginSuccess = { navController.navigate(Route.Auth.MainScreen) },
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
                onRegisterSuccess = {email ->
                    //received email value straight to your type-safe route object
                    navController.navigate(Route.Auth.OtpVerify(email)) },
                onNavigateToLogin = {navController.navigate(Route.Auth.Login())},
            )
        }

        composable<Route.Auth.ForgotPassword>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
            ForgotPasswordScreen(
                onBack = {navController.navigate(Route.Auth.Login())},
                onSendOtp = {navController.navigate(Route.Auth.PasswordRecoveryScreen)},
            )
        }

        composable<Route.Auth.OtpVerify>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {backStackEntry ->
            // Type-safely extract the arguments from the route instance
            val otpRoute = backStackEntry.toRoute<Route.Auth.OtpVerify>()
            OtpVerifyScreen(
                onBack = {
                    // 1. Pass the email back to login, clearing the OTP screen from history
                    navController.navigate(Route.Auth.Login(email = otpRoute.email)) {
                        popUpTo<Route.Auth.OtpVerify> { inclusive = true }
                    }},
                email = otpRoute.email, // Use the extracted email string here
                onVerified = {  // 2. Also pass it if you want them to log in automatically after verification
                    navController.navigate(Route.Auth.Login(email = otpRoute.email)) {
                        popUpTo<Route.AuthGraph> { inclusive = true }
                    }}
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
                onBack = {navController.navigate(Route.Auth.Login())},
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
                onPasswordSet = {navController.navigate(Route.Auth.Login())}
            )
        }

        composable<Route.Auth.MainScreen>(
            enterTransition = NavAnimations.enterSlideIn,
            exitTransition = NavAnimations.exitSlideOut,
            popEnterTransition = NavAnimations.popEnterSlideIn,
            popExitTransition = NavAnimations.popExitSlideOut,
        ) {
          MainScreen()
        }
    }
}
