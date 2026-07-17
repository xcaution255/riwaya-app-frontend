package com.excaution.riwayaapp.presentation.navigation

import kotlinx.serialization.Serializable

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
        data class OtpVerify(val email: String) : Auth

        @Serializable
        data object Splash : Auth

        @Serializable
        data object Onboarding : Auth {
        }

        @Serializable
        data object PasswordRecoveryScreen

        @Serializable
        data object MainScreen
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

        @Serializable
        data object Search : Main {

        }
    }
}
