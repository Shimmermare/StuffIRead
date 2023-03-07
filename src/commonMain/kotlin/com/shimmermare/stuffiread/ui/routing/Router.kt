package com.shimmermare.stuffiread.ui.routing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.aakira.napier.Napier

/**
 * Basic page based navigation.
 * Does not support history / backtracking.
 *
 * Q: Why write custom navigation instead of provided with Compose?
 * A: Compose navigation package (androidx.navigation:navigation-compose) is currently supported only on Android.
 */
class Router {
    var currentPage: Page by mutableStateOf(InitialPage)
        private set

    fun <P : Page> goTo(page: P) {
        currentPage = page
        Napier.i { "Going to page $page" }
    }

    @Composable
    fun CurrentPageTitle() = currentPage.Title()

    @Composable
    fun CurrentPageBody() = currentPage.Body()
}

private object InitialPage : Page {
    @Composable
    override fun Body() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}