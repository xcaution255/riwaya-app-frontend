package com.excaution.riwayaapp.core.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
        modules(networkModule)
        modules(authModule)
        modules(profileModule)
        modules(postModule)
    }
}