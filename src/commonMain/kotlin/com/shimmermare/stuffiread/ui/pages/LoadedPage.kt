package com.shimmermare.stuffiread.ui.pages

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.util.LoadingContent
import io.github.aakira.napier.Napier

/**
 * Page with content that is asynchronously loaded.
 */
abstract class LoadedPage<Data : PageData, Loadable> : Page<Data> {
    @Composable
    override fun Body(router: Router, app: AppState, data: Data) {
        LoadingContent(
            key = router.currentPage to router.currentData,
            loader = { load(app, data) },
            onError = { LoadingError(data, it) },
        ) { loaded ->
            AnimatedFadeIn {
                LoadedContent(router, app, loaded)
            }
        }
    }

    protected abstract suspend fun load(app: AppState, data: Data): Loadable

    @Composable
    protected open fun LoadingError(data: Data, e: Exception?) {
        Napier.e(e) { "Failed to load page content" }
        Text("Failed to load page content", style = MaterialTheme.typography.h5)
    }

    @Composable
    protected abstract fun LoadedContent(router: Router, app: AppState, loaded: Loadable)
}