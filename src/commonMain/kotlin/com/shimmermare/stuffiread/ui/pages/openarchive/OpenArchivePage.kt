package com.shimmermare.stuffiread.ui.pages.openarchive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Page
import io.github.aakira.napier.Napier

class OpenArchivePage : Page {
    @Composable
    override fun Title() {
        Text("Stuff I Read")
    }

    @Composable
    override fun Body() {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            ArchiveDirectorySelector { archiveDirectory, createIfNotExists ->
                try {
                    StoryArchiveHolder.openStoryArchive(archiveDirectory, createIfNotExists)
                } catch (e: Exception) {
                    Napier.e(e) { "Failed to open story archive $archiveDirectory" }
                    Router.goTo(
                        ErrorPage(
                            title = "Failed to open story archive",
                            description = "Story archive: $archiveDirectory",
                            exception = e,
                        )
                    )
                }
            }
        }
    }
}