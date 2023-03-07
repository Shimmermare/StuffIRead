package com.shimmermare.stuffiread.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.routing.Page
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Page with content that is asynchronously loaded.
 */
abstract class LoadedPage<Loadable>(
    private val timeout: Duration = 5.seconds
) : Page {
    private var reloadNeeded: Boolean by mutableStateOf(false)

    protected var status: Status by mutableStateOf(Status.LOADING)
    protected var content: Loadable? by mutableStateOf(null)
    protected var error: Exception? by mutableStateOf(null)

    @Composable
    override fun Body() {
        LaunchedEffect(this, reloadNeeded) {
            if (status != Status.LOADING && !reloadNeeded) {
                return@LaunchedEffect
            }

            reloadNeeded = false
            status = Status.LOADING
            content = null
            error = null

            try {
                content = withTimeout(timeout) { load() }
                status = Status.LOADED
            } catch (e: Exception) {
                error = e
                status = Status.FAILED
            }
        }

        when (status) {
            Status.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Status.LOADED -> {
                AnimatedFadeIn(key = this) {
                    LoadedContent()
                }
            }

            Status.FAILED -> {
                LoadingError()
            }
        }
    }

    protected abstract suspend fun load(): Loadable

    @Composable
    protected open fun LoadingError() {
        Napier.e(error) { "Failed to load page content" }
        Text("Failed to load page content", style = MaterialTheme.typography.h5)
    }

    @Composable
    protected abstract fun LoadedContent()

    protected fun reload() {
        reloadNeeded = true
    }

    enum class Status {
        LOADING,
        LOADED,
        FAILED
    }
}