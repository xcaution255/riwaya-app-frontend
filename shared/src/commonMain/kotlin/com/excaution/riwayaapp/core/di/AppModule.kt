package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.presentation.navigation.bottombar.BottomViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel {
        BottomViewModel()
    }
}