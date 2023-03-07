package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.LocalThemeProvider
import com.shimmermare.stuffiread.ui.theme.Theme
import io.github.aakira.napier.Napier

val Router: Router = Router()

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        val lastOpenArchive = AppSettingsHolder.settings.recentlyOpenedArchives.firstOrNull()
        if (lastOpenArchive != null) {
            try {
                StoryArchiveHolder.openStoryArchive(lastOpenArchive, false)
            } catch (e: Exception) {
                Napier.e(e) { "Failed to open previous story archive: $lastOpenArchive" }
                Router.goTo(
                    ErrorPage(
                        title = "Failed to open story archive that was last opened previously",
                        description = "Story archive: $lastOpenArchive",
                        exception = e,
                    )
                )
            }
        }
    }

    LocalThemeProvider {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    MaterialTheme(
        colors = when (LocalTheme.current) {
            Theme.LIGHT -> lightColors()
            Theme.DARK -> darkColors()
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onResetAppStateRequest = { StoryArchiveHolder.closeStoryArchive() }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Router.CurrentPageBody()
            }
        }
    }
}