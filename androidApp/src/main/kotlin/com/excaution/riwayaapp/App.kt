package com.excaution.riwayaapp

import android.app.Application
import com.excaution.riwayaapp.di.initKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}