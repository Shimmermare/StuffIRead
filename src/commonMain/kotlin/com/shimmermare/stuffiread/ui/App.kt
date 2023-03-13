package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.theme.DarkColors
import com.shimmermare.stuffiread.ui.theme.DarkExtendedColors
import com.shimmermare.stuffiread.ui.theme.LightColors
import com.shimmermare.stuffiread.ui.theme.LightExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.LocalThemeProvider
import com.shimmermare.stuffiread.ui.theme.Theme

val Router: Router = Router()

@Composable
@Preview
fun App() {
    var wasOnOpenArchivePageBefore: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(StoryArchiveHolder.storyArchive) {
        if (StoryArchiveHolder.isOpen) {
            Router.goTo(StoriesPage())
        } else {
            Router.goTo(OpenArchivePage(onAppStart = !wasOnOpenArchivePageBefore))
            wasOnOpenArchivePageBefore = true
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