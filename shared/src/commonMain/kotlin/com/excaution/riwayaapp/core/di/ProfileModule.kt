package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.data.profile.ProfileApi
import com.excaution.riwayaapp.data.profile.ProfileRepository
import com.excaution.riwayaapp.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single { ProfileApi(get()) }
    single { ProfileRepository(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}