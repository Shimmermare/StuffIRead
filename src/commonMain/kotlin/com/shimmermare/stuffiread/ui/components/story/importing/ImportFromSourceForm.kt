package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.shimmermare.stuffiread.importer.pastebin.PasteKey
import com.shimmermare.stuffiread.ui.components.input.OutlinedEnumField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn

@Composable
fun ImportFromSourceForm(
    onSourceSelected: (ImportSource?) -> Unit,
    onImported: (ImportedStory) -> Unit
) {
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
                    if (importSource != it) {
                        onSourceSelected(it)
                        importSource = it
                    }
                }
            )
        }
        VerticalScrollColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            importSource?.let { importSource ->
                when (importSource) {
                    ImportSource.PASTEBIN -> PasteImportForm<PasteKey>(
                        source = importSource,
                        examplePasteUrls = listOf("https://pastebin.com/NdYBi384", "https://pastebin.com/raw/NdYBi384"),
                        onImported = onImported
                    )
                    ImportSource.PONEPASTE -> PasteImportForm<PasteKey>(
                        source = importSource,
                        examplePasteUrls = listOf("https://ponepaste.org/5680", "https://ponepaste.org/raw/5680"),
                        onImported = onImported
                    )
                    ImportSource.PONEBIN -> PasteImportForm<PasteKey>(
                        source = importSource,
                        examplePasteUrls = listOf("https://poneb.in/ABCDEFGH", "https://poneb.in/raw/ABCDEFGH"),
                        onImported = onImported
                    )
                    else -> Text("NOT IMPLEMENTED")
                }
            }
        }
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