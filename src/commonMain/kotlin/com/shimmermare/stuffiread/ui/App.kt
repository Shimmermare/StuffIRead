package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router
import java.nio.file.Path

@Composable
@Preview
fun App(initialDbFile: Path? = null) {
    var app: AppState? by remember { mutableStateOf(initialDbFile?.let { AppState(it, false) }) }

    DisposableEffect(app) {
        val captured = app
        onDispose {
            captured?.close()
        }
    }

    MaterialTheme {
        if (app == null) {
            AppWithoutState(onStateSet = { app = it })
        } else {
            AppWithState(app!!, onResetAppStateRequest = { app = null })
        }
    }
}

@Composable
private fun AppWithoutState(onStateSet: (AppState?) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stuff I Read") })
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
        ) {
            StoryDatabaseSelector { onStateSet.invoke(it) }
        }
    }
}

@Composable
private fun AppWithState(app: AppState, onResetAppStateRequest: () -> Unit) {
    val router: Router = remember { Router.create(app, StoriesPage, EmptyData) }

    Scaffold(topBar = {
        TopBar(
            router = router, onResetAppStateRequest
        )
    }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            router.renderBody()
        }
    }
}