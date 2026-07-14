package com.excaution.riwayaapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClient = HttpClient(OkHttp)