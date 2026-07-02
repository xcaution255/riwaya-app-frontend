package com.excaution.riwayaapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform