package com.shimmermare.stuffiread.ui.pages.story.create.importing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.importer.ImportSource
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.ui.components.input.OutlinedEnumField

@Composable
fun StoryImportForm(onOpenStateChange: (Boolean) -> Unit, onImported: (ImportedStory) -> Unit) {
    var importSource: ImportSource? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Import from")
            OutlinedEnumField(
                value = importSource,
                enumType = ImportSource::class,
                displayNameProvider = { it.getDisplayName() },
                canBeCleared = true,
                inputFieldModifier = Modifier.width(300.dp).height(36.dp),
                onValueChange = {
                    if (importSource != it && (importSource == null || it == null)) onOpenStateChange(it != null)
                    importSource = it
                }
            )
        }
        importSource?.let { importSource -> ImportSourceForm(importSource, onImported) }
    }
}

private fun ImportSource.getDisplayName(): String {
    return when (this) {
        ImportSource.ARCHIVE_OF_OUR_OWN -> "archiveofourown.org"
        ImportSource.PASTEBIN -> "pastebin.com"
        ImportSource.PONEBIN -> "poneb.in"
        ImportSource.FIMFICTION -> "FimFiction.net"
        ImportSource.PONEPASTE -> "ponepaste.org"
        ImportSource.FICBOOK -> "ficbook.net"
        ImportSource.PONYFICTION -> "ponyfiction.org"
    }
}