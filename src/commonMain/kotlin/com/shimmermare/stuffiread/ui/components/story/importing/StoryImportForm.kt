package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.importer.ImportSource
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.ui.components.story.StoryFormData

@Composable
fun StoryImportForm(
    onSourceSelected: (ImportSource?) -> Unit,
    onImported: (StoryFormData) -> Unit
) {
    var importedStory: ImportedStory? by remember { mutableStateOf(null) }

    if (importedStory == null) {
        ImportFromSourceForm(onSourceSelected) { importedStory = it }
    } else {
        TagMappingForm(importedStory!!, onImported)
    }
}