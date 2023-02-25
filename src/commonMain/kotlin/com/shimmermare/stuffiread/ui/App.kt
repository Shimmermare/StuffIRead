package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import java.nio.file.Path

@Composable
@Preview
fun App(initialArchiveDirectory: Path? = null) {
    val app: AppState = remember { AppState() }

    LaunchedEffect(initialArchiveDirectory) {
        if (initialArchiveDirectory != null) {
            try {
                app.openStoryArchive(initialArchiveDirectory, false)
            } catch (e: Exception) {
                app.router.goTo(
                    ErrorPage(
                        title = "Failed to open story archive passed with CLI args",
                        description = "Story archive: $initialArchiveDirectory",
                        exception = e,
                    )
                )
            }
        }
    }


    MaterialTheme {
        Scaffold(
            topBar = {
                TopBar(
                    app = app,
                    onResetAppStateRequest = { app.closeStoryArchive() }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                app.router.CurrentPageBody()
            }
        }
    }
}