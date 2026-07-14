package com.excaution.riwayaapp

import com.russhwolf.settings.Settings


expect fun Double.format(decimals: Int): String

expect fun createSettings(): Settings

expect fun getPlatformName(): String
