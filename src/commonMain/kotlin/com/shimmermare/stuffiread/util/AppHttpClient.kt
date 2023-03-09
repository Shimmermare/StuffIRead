package com.shimmermare.stuffiread.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*

val AppHttpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 20000
    }
}