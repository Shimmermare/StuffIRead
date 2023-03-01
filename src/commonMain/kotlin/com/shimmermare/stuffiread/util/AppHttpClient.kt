package com.shimmermare.stuffiread.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*

val AppHttpClient = HttpClient(CIO) {
}