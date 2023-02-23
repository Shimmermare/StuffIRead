package com.shimmermare.stuffiread.ui.pages.openarchive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.routing.Page

class OpenArchivePage : Page {
    @Composable
    override fun Title(app: AppState) {
        Text("Stuff I Read")
    }

    @Composable
    override fun Body(app: AppState) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            ArchiveDirectorySelector { archiveDirectory, createIfNotExists ->
                app.openStoryArchive(archiveDirectory, createIfNotExists)
            }
        }
    }
}