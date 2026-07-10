package com.excaution.riwayaapp
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun Double.format(decimals: Int): String {
    return NSString.stringWithFormat("%.${decimals}f", this)
}
