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
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://10.0.2.2:8080"

// 1. Create a lightweight, raw client purely for refreshing tokens.
// This client has NO Auth plugin installed, completely avoiding threading deadlocks.
private val standaloneRefreshClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    defaultRequest {
        url(BASE_URL)
        contentType(ContentType.Application.Json)
    }
}

// 3. Isolated extension function mapped to the standalone client engine context
suspend fun HttpClient.executeRefreshCall(refreshToken: String): RefreshResponse? = try {
    post("/auth/refresh") {
        setBody(RefreshRequest(refreshToken))
    }.body<RefreshResponse>()
} catch (e: Exception) {
    null
}

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
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
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
                // CIRCUIT BREAKER: Break the loop if the refresh endpoint itself fails
                if (response.request.url.encodedPath.contains("/auth/refresh")) {
                    tokenStorage.clearTokens()
                    return@refreshTokens null
                }

                val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null

                // 2. FIX: Call our standalone client to execute the token swap safely!
                val newTokens = standaloneRefreshClient.executeRefreshCall(refreshToken)

                if (newTokens != null) {
                    tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                } else {
                    tokenStorage.clearTokens()
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

// REFRESH CALL: Explicitly removes old, expired authorization headers
suspend fun HttpClient.refreshAccessToken(refreshToken: String): RefreshResponse? = try {
    post("/auth/refresh") {
        // Remove the inherited expired Authorization header completely
        headers.remove(HttpHeaders.Authorization)
        setBody(RefreshRequest(refreshToken))
    }.body<RefreshResponse>()
} catch (e: Exception) {
    null
}