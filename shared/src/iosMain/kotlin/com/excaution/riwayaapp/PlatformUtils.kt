package com.excaution.riwayaapp
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSString
import platform.Foundation.NSUserDefaults
import platform.Foundation.stringWithFormat

actual fun Double.format(decimals: Int): String {
    return NSString.stringWithFormat("%.${decimals}f", this)
}

actual fun createSettings(): Settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
//or you can use as for android main

actual fun getPlatformName(): String = "iOS"