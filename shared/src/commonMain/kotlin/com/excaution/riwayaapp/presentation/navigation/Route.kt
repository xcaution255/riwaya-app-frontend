package com.excaution.riwayaapp.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes (Navigation-Compose 2.8+ style).
 * Every screen is a @Serializable object/class instead of a raw string,
 * so typos and argument mismatches are caught at compile time.
 */
sealed interface Route {

    @Serializable
    data object AuthGraph : Route

    @Serializable
    data object MainGraph : Route

    sealed interface Auth : Route {
        @Serializable
        data object Login : Auth

        @Serializable
        data object Register : Auth

        @Serializable
        data object ForgotPassword : Auth

        @Serializable
        data object VerifyEmail : Auth

        @Serializable
        data object OtpVerify : Auth

        @Serializable
        data object Splash : Auth

        @Serializable
        data object Onboarding : Auth {
        }

        @Serializable
        data object PasswordRecoveryScreen

    }

    sealed interface Main : Route {
        @Serializable
        data object Home : Main

        @Serializable
        data object BookShop : Main

        @Serializable
        data object Profile : Main

        @Serializable
        data object Notifications : Main

        @Serializable
        data object Saved : Main
    }
}
