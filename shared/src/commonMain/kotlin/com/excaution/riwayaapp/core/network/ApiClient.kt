package com.excaution.riwayaapp.core.network

import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.data.auth.RefreshRequest
import com.excaution.riwayaapp.data.auth.RefreshResponse
import com.excaution.riwayaapp.getPlatformName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://10.0.2.2:8080"

fun buildHttpClient(
    engineClient: HttpClient,
    tokenStorage: TokenStorage,
): HttpClient = engineClient.config {
    // 1. JSON serialization
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true   // don't crash if backend adds fields
            isLenient = true
            encodeDefaults = true
            explicitNulls = false
            prettyPrint = true
        })
    }

    // 2. Timeouts — never let a request hang forever
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }

    // 3. Logging — strip in release builds (see note below)
    install(Logging) {
        level = LogLevel.INFO
    }

    // 4. Auth plugin — auto-attaches Bearer token, auto-refreshes on 401
    install(Auth) {
        bearer {
            //Stops Ktor from saving old tokens in its internal memory loop
            cacheTokens = false
            loadTokens {
                val access = tokenStorage.getAccessToken()
                val refresh = tokenStorage.getRefreshToken()
                if (access != null) BearerTokens(access, refresh.orEmpty()) else null
            }
            refreshTokens {
                // called automatically by Ktor when a request gets 401
                val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null
                val newTokens = client.refreshAccessToken(refreshToken) // plain, unauthenticated call
                if (newTokens != null) {
                    tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                } else {
                    tokenStorage.clearTokens() // refresh failed -> force logout upstream
                    null
                }
            }

            // Only send the token to your own API, never to third parties
            sendWithoutRequest { request ->
                request.url.host == "10.0.2.2" && request.url.port == 8080
            }
        }
    }

    defaultRequest {
        url(BASE_URL)
        contentType(ContentType.Application.Json)
        header("X-Client-Platform", getPlatformName())
    }
}

suspend fun HttpClient.refreshAccessToken(refreshToken: String): RefreshResponse? = try {
    post("/auth/refresh") {
        setBody(RefreshRequest(refreshToken))
    }.body<RefreshResponse>()
} catch (e: Exception) {
    null
}