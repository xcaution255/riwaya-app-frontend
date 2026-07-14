package com.excaution.riwayaapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClientEngine(): HttpClient = HttpClient(Darwin)