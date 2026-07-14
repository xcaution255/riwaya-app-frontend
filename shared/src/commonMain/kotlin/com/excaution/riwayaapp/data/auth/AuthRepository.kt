package com.excaution.riwayaapp.data.auth

import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) {
    private val _isLoggedIn = MutableStateFlow(tokenStorage.isLoggedIn())
    val isLoggedIn = _isLoggedIn.asStateFlow()

    suspend fun register(email: String, password: String, name: String) =
        api.register(email, password, name)

    suspend fun verifyEmailOtp(email: String, otp: String) =
        api.verifyEmailOtp(email, otp)

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val result = api.login(email, password)
        if (result is ApiResult.Success) {
            tokenStorage.saveTokens(result.data.accessToken, result.data.refreshToken)
            _isLoggedIn.value = true
        }
        return result
    }

    suspend fun forgotPassword(email: String) = api.forgotPassword(email)

    suspend fun verifyResetOtp(email: String, otp: String) = api.verifyResetOtp(email, otp)

    suspend fun resetPassword(resetToken: String, newPassword: String) =
        api.resetPassword(resetToken, newPassword)

    suspend fun apiStatus() = api.apiStatus()


    fun logout() {
        tokenStorage.clearTokens()
        _isLoggedIn.value = false
    }

}