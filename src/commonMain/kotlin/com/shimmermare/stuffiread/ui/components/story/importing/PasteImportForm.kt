package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.importer.ImportSource
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.UrlParser
import com.shimmermare.stuffiread.importer.pastebased.PasteBasedStoryImporter
import com.shimmermare.stuffiread.importer.pastebased.PasteImportSettings
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult

@Suppress("UNCHECKED_CAST")
@Composable
fun <PasteId> PasteImportForm(
    source: ImportSource,
    examplePasteUrls: List<String>,
    onImported: (ImportedStory) -> Unit
) {
    val urlParser = source.urlParser as UrlParser<PasteId>
    val importer = source.importer as PasteBasedStoryImporter<PasteId>

    BaseSourceImportForm(
        source = source,
        defaultData = { PasteImportFormData() },
        importer = { formData ->
            val importSettings = PasteImportSettings(
                pasteIds = formData.urls.map { urlParser.parse(it) }
            )
            importer.import(importSettings)
        },
        onImported = onImported
    ) { state ->
        PasteUrlsFormField(urlParser, examplePasteUrls, state)
    }
}

@Composable
private fun <PasteId> PasteUrlsFormField(
    urlParser: UrlParser<PasteId>,
    examplePasteUrls: List<String>,
    state: InputFormState<PasteImportFormData>
) {
    TextFormField(
        id = "urls",
        state = state,
        name = "Paste URLs",
        description = """Specify one or multiple paste URLs (one per line).
            |Multiple pastes are treated as single story.
            |Examples: ${examplePasteUrls.joinToString(", ")}""".trimMargin(),
        getter = { it.urls.joinToString(separator = "\n") },
        setter = { data, value ->
            data.copy(urls = value.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList())
        },
        validator = { urlsText ->
            if (urlsText.isBlank()) {
                return@TextFormField ValidationResult(false, "At least one paste URL is required")
            }
            val errors = mutableListOf<Pair<Int, String>>()
            val urls = urlsText.lineSequence().map { it.trim() }.toList()
            val validUrls = mutableSetOf<String>()
            urls.forEachIndexed { line, url ->
                if (url.isEmpty()) return@forEachIndexed
                if (!urlParser.matches(url)) {
                    errors.add(line to "Invalid paste URL")
                    return@forEachIndexed
                }
                if (validUrls.contains(url)) {
                    errors.add(line to "Duplicate paste URL")
                    return@forEachIndexed
                }
                validUrls.add(url)
            }
            val errorMessage = if (errors.isEmpty()) {
                null
            } else {
                errors.joinToString(separator = "\n") { (line, error) ->
                    error + if (urls.size > 1) " at line ${line + 1}" else ""
                }
            }
            ValidationResult(errors.isEmpty(), errorMessage)
        },
        singleLine = false,
        textInputModifier = Modifier.fillMaxWidth().sizeIn(minHeight = 108.dp, maxHeight = 420.dp),
    )
}

private data class PasteImportFormData(
    var urls: List<String> = emptyList()
)