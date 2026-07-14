package com.excaution.riwayaapp.core.network

import io.ktor.client.statement.bodyAsText

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int?, val message: String) : ApiResult<Nothing>()
    data object NetworkError : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: io.ktor.client.plugins.ClientRequestException) {
    // 4xx — try to pull a server error message
    val body = runCatching { e.response.bodyAsText() }.getOrNull()
    ApiResult.Error(e.response.status.value, body ?: e.message.orEmpty())
} catch (e: io.ktor.client.plugins.ServerResponseException) {
    // 5xx
    ApiResult.Error(e.response.status.value, "Server error, please try again")
} catch (e: io.ktor.client.network.sockets.SocketTimeoutException) {
    ApiResult.NetworkError
} catch (e: io.ktor.utils.io.errors.IOException) {
    ApiResult.NetworkError
} catch (e: Exception) {
    ApiResult.Error(null, e.message ?: "Unknown error")
}