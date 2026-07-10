package com.excaution.riwayaapp


actual fun Double.format(decimals: Int): String {
    return java.lang.String.format(java.util.Locale.US, "%.${decimals}f", this)
}
