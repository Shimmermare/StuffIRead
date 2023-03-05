package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.StoryArchive
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.stories.StorySearchService
import com.shimmermare.stuffiread.stories.StoryService
import com.shimmermare.stuffiread.stories.cover.StoryCoverService
import com.shimmermare.stuffiread.stories.file.StoryFilesService
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.LocalThemeProvider
import com.shimmermare.stuffiread.ui.theme.Theme
import com.shimmermare.stuffiread.ui.util.compositionLocalOrThrow
import com.shimmermare.stuffiread.ui.util.staticCompositionLocalOrThrow
import java.nio.file.Path

const val GITHUB_URL = "https://github.com/Shimmermare/StuffIRead"

val LocalRouter: ProvidableCompositionLocal<Router> = staticCompositionLocalOrThrow()
val router @Composable get() = LocalRouter.current

val LocalAppSettings: ProvidableCompositionLocal<AppSettings> = compositionLocalOrThrow()
val appSettings @Composable get() = LocalAppSettings.current

val LocalStoryArchive: ProvidableCompositionLocal<StoryArchive?> = staticCompositionLocalOf { null }
val storyArchive @Composable get() = LocalStoryArchive.current

val LocalStoryService: ProvidableCompositionLocal<StoryService> = staticCompositionLocalOrThrow()
val storyService @Composable get() = LocalStoryService.current

val LocalStoryCoverService: ProvidableCompositionLocal<StoryCoverService> = staticCompositionLocalOrThrow()
val storyCoverService @Composable get() = LocalStoryCoverService.current

val LocalStoryFilesService: ProvidableCompositionLocal<StoryFilesService> = staticCompositionLocalOrThrow()
val storyFilesService @Composable get() = LocalStoryFilesService.current

val LocalTagService: ProvidableCompositionLocal<TagService> = staticCompositionLocalOrThrow()
val tagService @Composable get() = LocalTagService.current

val LocalStorySearchService: ProvidableCompositionLocal<StorySearchService> = staticCompositionLocalOrThrow()
val storySearchService @Composable get() = LocalStorySearchService.current

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

    val locals = buildList<ProvidedValue<*>> {
        add(LocalRouter provides app.router)
        add(LocalAppSettings provides app.settings)

        app.storyArchive?.let {
            add(LocalStoryArchive provides it)
            add(LocalStoryService provides it.storyService)
            add(LocalStoryCoverService provides it.storyCoverService)
            add(LocalStoryFilesService provides it.storyFilesService)
            add(LocalTagService provides it.tagService)
            add(LocalStorySearchService provides it.storySearchService)
        }
    }.toTypedArray()

    CompositionLocalProvider(*locals) {
        LocalThemeProvider {
            AppContent(app)
        }
    }
}

@Composable
private fun AppContent(app: AppState) {
    MaterialTheme(
        colors = when (LocalTheme.current) {
            Theme.LIGHT -> lightColors()
            Theme.DARK -> darkColors()
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onResetAppStateRequest = { app.closeStoryArchive() }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                router.CurrentPageBody()
            }
        }
    }
}