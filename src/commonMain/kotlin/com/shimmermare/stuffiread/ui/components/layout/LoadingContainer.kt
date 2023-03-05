package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.layout.Box
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Same as [OptionalLoadingContainer] but [loader] is not allowed to return null.
 */
@Composable
inline fun <K, V> LoadingContainer(
    key: K,
    timeout: Duration = 5.seconds,
    crossinline loader: suspend CoroutineScope.(K) -> V,
    crossinline onError: @Composable (Exception) -> Unit = {
        Text(
            text = "Loading failed. Key: $key",
            color = MaterialTheme.colors.error
        )
    },
    crossinline content: @Composable (V) -> Unit
) {
    OptionalLoadingContainer<K, V>(
        key = key,
        timeout = timeout,
        loader = { loader(it) ?: throw IllegalStateException("Loaded value is null") },
        onError = onError,
        content = { content(it!!) }
    )
}

/**
 * Async load a value and then use it to display [content].
 * Will show loading indicator while in loading and error if loading failed.
 */
@Composable
inline fun <K, V> OptionalLoadingContainer(
    key: K,
    timeout: Duration = 5.seconds,
    crossinline loader: suspend CoroutineScope.(K) -> V?,
    crossinline onError: @Composable (Exception) -> Unit = {
        Text(
            text = "Loading failed. Key: $key",
            color = MaterialTheme.colors.error
        )
    },
    crossinline content: @Composable (V?) -> Unit
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
            value = null
        }

        loading = false
    }

    if (loading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (error != null) {
            onError(error!!)
        } else {
            content(value)
        }
    }
}