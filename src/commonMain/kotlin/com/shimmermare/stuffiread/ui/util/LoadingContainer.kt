package com.shimmermare.stuffiread.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Async load a value and then use it to display [content].
 * Will show loading indicator while in loading and error if loading failed.
 */
@Composable
fun <K, V> LoadingContainer(
    key: K,
    timeout: Duration = 5.seconds,
    loader: suspend CoroutineScope.(K) -> V,
    onError: @Composable (Exception?) -> Unit = { Text("Loading failed. Key: $key", style = MaterialTheme.typography.h5) },
    content: @Composable (V) -> Unit
) {
    var loading: Boolean by remember(key) { mutableStateOf(true) }
    var error: Exception? by remember(key) { mutableStateOf(null) }
    var value: V? by remember(key) { mutableStateOf(null) }

    LaunchedEffect(key) {
        try {
            value = withTimeout(timeout) {
                loader(key)
            }
        } catch (e: Exception) {
            error = e
        }

        loading = false
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (value == null) {
            onError(error)
        } else {
            content(value!!)
        }
    }
}