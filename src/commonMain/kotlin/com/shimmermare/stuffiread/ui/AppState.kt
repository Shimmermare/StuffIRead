package com.shimmermare.stuffiread.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.StoryArchive
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.settings.SettingsService
import com.shimmermare.stuffiread.settings.SettingsServiceImpl
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.routing.Router
import java.nio.file.Path

class AppState {
    private val settingsService: SettingsService = SettingsServiceImpl()

    var settings: AppSettings by mutableStateOf(settingsService.getSettings())
        private set

    val router: Router = Router(this, OpenArchivePage())

    var storyArchive: StoryArchive? by mutableStateOf(null)
        private set

    fun updateSettings(settings: AppSettings) {
        settingsService.updateSettings(settings)
        this.settings = settings
    }

    fun resetSettings() {
        this.settings = settingsService.resetSettings()
    }

    fun openStoryArchive(archiveDirectory: Path, createIfNotExists: Boolean) {
        storyArchive = StoryArchive(archiveDirectory, createIfNotExists)
        router.goTo(StoriesPage())
    }

    fun closeStoryArchive() {
        storyArchive = null
        router.goTo(OpenArchivePage())
    }

    companion object {
        const val GITHUB_URL = "https://github.com/Shimmermare/StuffIRead"
    }
}

