package com.excaution.riwayaapp

import com.russhwolf.settings.Settings



actual fun Double.format(decimals: Int): String {
    return java.lang.String.format(java.util.Locale.US, "%.${decimals}f", this)
}

actual fun createSettings(): Settings {return Settings()}

actual fun getPlatformName(): String = "Android"