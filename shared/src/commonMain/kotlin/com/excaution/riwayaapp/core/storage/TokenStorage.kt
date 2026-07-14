package com.excaution.riwayaapp.core.storage

import com.russhwolf.settings.Settings


class TokenStorage(private val settings: Settings) {
    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS)
    fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH)

    fun saveTokens(access: String, refresh: String) {
        settings.putString(KEY_ACCESS, access)
        settings.putString(KEY_REFRESH, refresh)
    }

    fun clearTokens() {
        settings.remove(KEY_ACCESS)
        settings.remove(KEY_REFRESH)
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}