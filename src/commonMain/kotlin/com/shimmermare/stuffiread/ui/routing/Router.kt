package com.shimmermare.stuffiread.ui.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.ui.AppState
import io.github.aakira.napier.Napier

/**
 * Basic page based navigation.
 * Does not support history / backtracking.
 *
 * Q: Why write custom navigation instead of provided with Compose?
 * A: Compose navigation package (androidx.navigation:navigation-compose) is currently supported only on Android.
 */
class Router private constructor(
    private val app: AppState,
    startingPage: Page<out PageData>,
    startingData: PageData
) {
    var currentPage: Page<out PageData> by mutableStateOf(startingPage)
        private set

    /**
     * Type of [currentData] is derived from [currentPage].
     */
    var currentData: PageData by mutableStateOf(startingData)
        private set

    fun <P : Page<D>, D : PageData> goTo(page: P, data: D) {
        currentPage = page
        currentData = data
        Napier.i { "Going to page ${page::class} with data: $data" }
    }

    @Suppress("UNCHECKED_CAST")
    @Composable
    fun renderTitle() = (currentPage as Page<PageData>).renderTopBarTitle(app, currentData)

    @Suppress("UNCHECKED_CAST")
    @Composable
    fun renderBody() = (currentPage as Page<PageData>).renderBody(this, app, currentData)

    companion object {
        fun <P : Page<D>, D : PageData> create(app: AppState, startingPage: P, startingData: D): Router {
            @Suppress("UNCHECKED_CAST")
            return Router(app, startingPage as Page<PageData>, startingData)
        }
    }
}
