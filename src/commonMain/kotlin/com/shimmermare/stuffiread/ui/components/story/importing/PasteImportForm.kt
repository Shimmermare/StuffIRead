package com.shimmermare.stuffiread.ui.components.story.importing

import ResetFormButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.importer.ImportSource
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.UrlParser
import com.shimmermare.stuffiread.importer.pastebased.PasteBasedStoryImporter
import com.shimmermare.stuffiread.importer.pastebased.PasteImportSettings
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.components.error.ErrorInfo
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
@Composable
fun <PasteId> PasteImportForm(
    source: ImportSource,
    examplePasteUrls: List<String>,
    onImported: (ImportedStory) -> Unit
) {
    val urlParser = source.urlParser as UrlParser<PasteId>
    val importer = source.importer as PasteBasedStoryImporter<PasteId>

    val coroutineScope = rememberCoroutineScope()

    var formData: PasteImportFormData by remember { mutableStateOf(PasteImportFormData()) }
    var inProcess: Boolean by remember { mutableStateOf(false) }
    var error: ErrorInfo? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        error?.let { error ->
            ErrorCard(error, modifier = Modifier.width(600.dp))
        }

        if (inProcess) {
            Text("Importing...")
            CircularProgressIndicator()
        } else {
            SubmittableInputForm(
                data = formData,
                modifier = Modifier.width(600.dp),
                submitButtonText = "Import",
                canSubmitWithoutChanges = true,
                onSubmit = { data ->
                    formData = data

                    coroutineScope.launch {
                        val importSettings = data.toImportSettings(source.urlParser)
                        inProcess = true
                        try {
                            val story = importer.import(importSettings)
                            inProcess = false
                            onImported(story)
                        } catch (e: Exception) {
                            Napier.e(e) { "Failed to import story from $source with settings: $importSettings" }
                            inProcess = false
                            error = ErrorInfo(title = "Import failed", exception = e)
                        }
                    }
                },
                actions = {
                    ResetFormButton(
                        state = it,
                        originalData = PasteImportFormData(),
                        name = "Clear",
                    )
                }
            ) { state ->
                PasteUrlsFormField(urlParser, examplePasteUrls, state)
            }
        }
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
) {
    fun <PasteId> toImportSettings(urlParser: UrlParser<PasteId>): PasteImportSettings<PasteId> {
        return PasteImportSettings(
            pasteIds = urls.map { urlParser.parse(it) }
        )
    }
}