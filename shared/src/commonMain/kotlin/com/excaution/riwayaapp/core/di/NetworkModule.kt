package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.core.network.buildHttpClient
import com.excaution.riwayaapp.core.network.createHttpClientEngine
import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.createSettings
import org.koin.dsl.module

val networkModule = module {
    single { createSettings() }
    single { TokenStorage(get()) }
    single { buildHttpClient(createHttpClientEngine(), get()) }
}