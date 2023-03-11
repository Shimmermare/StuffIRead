package com.shimmermare.stuffiread.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.StoryArchive
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import java.nio.file.Path

object StoryArchiveHolder {
    var storyArchive: StoryArchive? by mutableStateOf(null)
        private set

    val isOpen: Boolean get() = storyArchive != null

    /**
     * @throws IllegalStateException if no archive is open
     */
    val openStoryArchive get() = storyArchive ?: error("No open story archive present")

    /**
     * @throws IllegalStateException if no archive is open
     */
    val storyService get() = openStoryArchive.storyService

    /**
     * @throws IllegalStateException if no archive is open
     */
    val storyCoverService get() = openStoryArchive.storyCoverService

    /**
     * @throws IllegalStateException if no archive is open
     */
    val storyFilesService get() = openStoryArchive.storyFilesService

    /**
     * @throws IllegalStateException if no archive is open
     */
    val tagService get() = openStoryArchive.tagService

    /**
     * @throws IllegalStateException if no archive is open
     */
    val tagMappingService get() = openStoryArchive.tagMappingService

    /**
     * @throws IllegalStateException if no archive is open
     */
    val storySearchService get() = openStoryArchive.storySearchService

    fun openStoryArchive(archiveDirectory: Path, createIfNotExists: Boolean) {
        storyArchive = StoryArchive(archiveDirectory, createIfNotExists)
        addRecentlyOpenedArchive(archiveDirectory)
        Router.goTo(StoriesPage())
    }

    fun closeStoryArchive() {
        storyArchive = null
        Router.goTo(OpenArchivePage())
    }

    private fun addRecentlyOpenedArchive(archiveDirectory: Path) {
        val currentRecentlyOpened = AppSettingsHolder.settings.recentlyOpenedArchives.toMutableList()

        if (currentRecentlyOpened.contains(archiveDirectory)) {
            // If already present in last opened - move to most recent
            if (currentRecentlyOpened[0] != archiveDirectory) {
                currentRecentlyOpened.remove(archiveDirectory)
                currentRecentlyOpened.add(0, archiveDirectory)
            }
        } else {
            currentRecentlyOpened.add(0, archiveDirectory)
        }

        AppSettingsHolder.update(
            AppSettingsHolder.settings.copy(
                recentlyOpenedArchives = currentRecentlyOpened.take(AppSettings.RECENTLY_OPENED_TO_KEEP.toInt())
            )
        )
    }
}