package com.shimmermare.stuffiread.ui.pages.openarchive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.ui.AppSettingsHolder
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.openStoryArchive
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyArchive
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.FileUtils
import com.shimmermare.stuffiread.util.FileUtils.directoryNotExistsOrEmpty
import io.github.aakira.napier.Napier
import java.nio.file.Path

/**
 * If this is first visit to page ([onAppStart] is true) and [AppSettings.openLastArchiveOnStartup] is enabled
 * - open last opened story archive if present
 *
 * Offer to open or create story archive directory.
 * If archive directory to open/create is empty (as in contains no files) - offer to add example content.
 */
class OpenArchivePage(
    private val onAppStart: Boolean = false
) : Page {
    @Composable
    override fun Title() {
        Text("Stuff I Read")
    }

    @Composable
    override fun Body() {
        var offerExampleContentFor: Path? by remember { mutableStateOf(null) }

        fun tryOpenStoryArchive(archiveDirectory: Path, offerExampleContentIfEmpty: Boolean = true) {
            try {
                if (offerExampleContentIfEmpty && archiveDirectory.directoryNotExistsOrEmpty()) {
                    offerExampleContentFor = archiveDirectory
                } else {
                    openStoryArchive(archiveDirectory)
                }
            } catch (e: Exception) {
                Napier.e(e) { "Failed to open story archive $archiveDirectory" }
                Router.goTo(
                    ErrorPage(
                        title = Strings.page_openArchive_error_failedToOpen_title.toString(),
                        description = Strings.page_openArchive_error_failedToOpen_description(archiveDirectory),
                        exception = e,
                    )
                )
            }
        }

        LaunchedEffect(this) {
            if (onAppStart) {
                val lastOpenArchive = AppSettingsHolder.settings.recentlyOpenedArchives.firstOrNull()
                if (AppSettingsHolder.settings.openLastArchiveOnStartup && lastOpenArchive != null) {
                    tryOpenStoryArchive(lastOpenArchive)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            ArchiveDirectorySelector { archiveDirectory ->
                tryOpenStoryArchive(archiveDirectory)
            }
        }

        offerExampleContentFor?.let { archiveDirectory ->
            ConfirmationDialog(
                title = { Text(Strings.page_openArchive_exampleContent_title.remember()) },
                dismissButtonText = Strings.page_openArchive_exampleContent_dismissButton.remember(),
                onDismissRequest = {
                    offerExampleContentFor = null
                    tryOpenStoryArchive(archiveDirectory, offerExampleContentIfEmpty = false)
                },
                confirmButtonText = Strings.page_openArchive_exampleContent_confirmButton.remember(),
                onConfirmRequest = {
                    offerExampleContentFor = null
                    copyExampleContentToArchive(archiveDirectory)
                    tryOpenStoryArchive(archiveDirectory)
                }
            ) {
                Text(Strings.page_openArchive_exampleContent_description.remember(archiveDirectory))
            }
        }
    }

    private fun copyExampleContentToArchive(archiveDirectory: Path) {
        require(storyArchive?.let { it.directory != archiveDirectory } ?: true) {
            "Can't copy example to currently open archive"
        }
        require(archiveDirectory.directoryNotExistsOrEmpty()) {
            "Can't copy example to non-empty directory"
        }
        FileUtils.copyFolderRecursiveFromClasspath("example-content", archiveDirectory)
    }
}