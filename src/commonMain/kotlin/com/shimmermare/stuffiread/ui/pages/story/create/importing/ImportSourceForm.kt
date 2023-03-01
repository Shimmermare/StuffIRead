package com.shimmermare.stuffiread.ui.pages.story.create.importing

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.importer.ImportSource
import com.shimmermare.stuffiread.importer.ImportedStory

@Composable
fun ImportSourceForm(source: ImportSource, onImported: (ImportedStory) -> Unit) {
    when (source) {
        ImportSource.PASTEBIN -> PastebinImportForm(onImported)
        else -> Text("NOT IMPLEMENTED")
    }
}