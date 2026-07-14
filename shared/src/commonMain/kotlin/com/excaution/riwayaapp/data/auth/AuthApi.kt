package com.excaution.riwayaapp.data.auth

import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.network.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(private val client: HttpClient) {

    suspend fun register(email: String, password: String, name: String): ApiResult<RegisterResponse> =
        safeApiCall {
            client.post("/auth/register") {
                setBody(RegisterRequest(email, password, name))
            }.body()
        }

    suspend fun verifyEmailOtp(email: String, otp: String): ApiResult<VerifyOtpResponse> =
        safeApiCall {
            client.post("/auth/verify-email") {
                setBody(VerifyOtpRequest(email, otp))
            }.body()
        }

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> =
        safeApiCall {
            client.post("/auth/login") {
                setBody(LoginRequest(email, password))
            }.body()
        }

    suspend fun forgotPassword(email: String): ApiResult<ForgotPasswordResponse> =
        safeApiCall {
            client.post("/auth/forgot-password") {
                setBody(ForgotPasswordRequest(email))
            }.body()
        }

    suspend fun verifyResetOtp(email: String, otp: String): ApiResult<VerifyResetOtpResponse> =
        safeApiCall {
            client.post("/auth/verify-reset-otp") {
                setBody(VerifyResetOtpRequest(email, otp))
            }.body()
        }

    suspend fun resetPassword(resetToken: String, newPassword: String): ApiResult<ResetPasswordResponse> =
        safeApiCall {
            client.post("/auth/reset-password") {
                setBody(ResetPasswordRequest(resetToken, newPassword))
            }.body()
        }

    suspend fun apiStatus(): ApiResult<MessageResponse> =
        safeApiCall {
            client.post("/auth/status") {
                setBody(MessageResponse(
                    message = "Android"                ))
            }.body()
        }
}