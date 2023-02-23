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
class Router(
    private val app: AppState,
    startingPage: Page,
) {
    var currentPage: Page by mutableStateOf(startingPage)
        private set

    fun <P : Page> goTo(page: P) {
        currentPage = page
        Napier.i { "Going to page $page" }
    }

    @Composable
    fun CurrentPageTitle() = currentPage.Title(app)

    @Composable
    fun CurrentPageBody() = currentPage.Body(app)
}
