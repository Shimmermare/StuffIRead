package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.settings.ThemeBehavior
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.nio.file.Path

@Composable
@Preview
fun App(initialArchiveDirectory: Path? = null) {
    val app: AppState = remember { AppState() }

    var theme: Theme by remember(app.settings) { mutableStateOf(Theme.fromBehavior(app.settings.themeBehavior)) }

    LaunchedEffect(app.settings) {
        if (app.settings.themeBehavior == ThemeBehavior.USE_SYSTEM) {
            while (isActive) {
                theme = Theme.fromBehavior(ThemeBehavior.USE_SYSTEM)
                delay(5000)
            }
        }
    }

    LocalContentAlpha
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

    CompositionLocalProvider(LocalTheme provides theme) {
        MaterialTheme(
            colors = when (LocalTheme.current) {
                Theme.LIGHT -> lightColors()
                Theme.DARK -> darkColors()
            }
        ) {
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
}