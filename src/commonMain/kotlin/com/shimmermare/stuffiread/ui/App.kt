package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.AppSettingsHolder.settings
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.theme.DarkColors
import com.shimmermare.stuffiread.ui.theme.DarkExtendedColors
import com.shimmermare.stuffiread.ui.theme.LightColors
import com.shimmermare.stuffiread.ui.theme.LightExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.LocalThemeProvider
import com.shimmermare.stuffiread.ui.theme.Theme
import io.github.aakira.napier.Napier

val Router: Router = Router()

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        val lastOpenArchive = settings.recentlyOpenedArchives.firstOrNull()
        if (settings.openLastArchiveOnStartup && lastOpenArchive != null) {
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
        } else {
            Router.goTo(OpenArchivePage())
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
            Theme.LIGHT -> LightColors
            Theme.DARK -> DarkColors
        }
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides when (LocalTheme.current) {
                Theme.LIGHT -> LightExtendedColors
                Theme.DARK -> DarkExtendedColors
            }
        ) {
            Scaffold(
                topBar = { TopBar() }
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
}