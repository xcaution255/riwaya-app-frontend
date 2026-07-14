package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.data.auth.AuthApi
import com.excaution.riwayaapp.data.auth.AuthRepository
import com.excaution.riwayaapp.presentation.auth.emailverify.VerifyEmailViewModel
import com.excaution.riwayaapp.presentation.auth.login.LoginViewModel
import com.excaution.riwayaapp.presentation.auth.otp.OtpVerifyViewModel
import com.excaution.riwayaapp.presentation.auth.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single { AuthApi(get()) }
    single { AuthRepository(get(), get()) }

    viewModel { RegisterViewModel(get()) }
    viewModel { VerifyEmailViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { OtpVerifyViewModel(get()) }
//    viewModel { ForgotPasswordViewModel(get()) }
//    viewModel { VerifyResetOtpViewModel(get()) }
//    viewModel { ResetPasswordViewModel(get()) }
}