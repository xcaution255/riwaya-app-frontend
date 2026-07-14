package com.excaution.riwayaapp.data.auth

import kotlinx.serialization.Serializable



@Serializable data class RegisterRequest(val email: String, val password: String, val username: String)
@Serializable data class RegisterResponse(val message: String)

@Serializable data class VerifyOtpRequest(val email: String, val otp: String)
@Serializable data class VerifyOtpResponse(val message: String)

@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class LoginResponse(val accessToken: String, val refreshToken: String, val role: String)

@Serializable data class ForgotPasswordRequest(val email: String)
@Serializable data class ForgotPasswordResponse(val message: String)

@Serializable data class VerifyResetOtpRequest(val email: String, val otp: String)
@Serializable data class VerifyResetOtpResponse(val resetToken: String) // short-lived token to authorize the reset

@Serializable data class ResetPasswordRequest(val resetToken: String, val newPassword: String)
@Serializable data class ResetPasswordResponse(val message: String)

@Serializable data class RefreshRequest(val refreshToken: String)
@Serializable data class TokenPairDto(val accessToken: String, val refreshToken: String)

@Serializable data class MessageResponse(val message: String)