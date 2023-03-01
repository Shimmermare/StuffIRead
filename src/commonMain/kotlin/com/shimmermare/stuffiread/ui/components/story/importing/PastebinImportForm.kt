package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
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
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.pastebin.PastebinImportSettings
import com.shimmermare.stuffiread.importer.pastebin.PastebinImporter
import com.shimmermare.stuffiread.importer.pastebin.PastebinUrlParser
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun PastebinImportForm(onImported: (ImportedStory) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    var inProcess: Boolean by remember { mutableStateOf(false) }
    var error: String? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        error?.let { error ->
            Text(text = "Import failed", color = MaterialTheme.colors.error, style = MaterialTheme.typography.h6)
            Text(text = error, color = MaterialTheme.colors.error)
        }

        if (inProcess) {
            Text("Importing...")
            CircularProgressIndicator()
        } else {
            SubmittableInputForm(
                data = PastebinImportFormData(),
                modifier = Modifier.width(600.dp),
                submitButtonText = "Import",
                canSubmitWithoutChanges = true,
                onSubmit = { data ->
                    coroutineScope.launch {
                        val importSettings = data.toImportSettings()
                        inProcess = true
                        try {
                            val story = PastebinImporter.import(importSettings)
                            inProcess = false
                            onImported(story)
                        } catch (e: Throwable) {
                            Napier.e(e) { "Failed to import story from Pastebin with settings: $importSettings" }
                            inProcess = false
                            error = "Import failed: $e"
                        }
                    }
                }
            ) { state ->
                PasteUrlsFormField(state)
            }
        }
    }
}

@Composable
private fun PasteUrlsFormField(state: InputFormState<PastebinImportFormData>) {
    TextFormField(
        id = "urls",
        state = state,
        name = "Paste URLs",
        description = """Specify one or multiple paste URLs (one per line).
            |Multiple pastes are treated as single story.
            |Examples: https://pastebin.com/NdYBi384, https://pastebin.com/raw/NdYBi384""".trimMargin(),
        getter = { it.urls.joinToString(separator = "\n") },
        setter = { data, value ->
            data.copy(urls = value.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList())
        },
        validator = { urlsText ->
            val errors = mutableListOf<Pair<Int, String>>()
            val urls = urlsText.lineSequence().map { it.trim() }.toList()
            val validUrls = mutableSetOf<String>()
            urls.forEachIndexed { line, url ->
                if (url.isEmpty()) return@forEachIndexed
                if (!PastebinUrlParser.matches(url)) {
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

data class PastebinImportFormData(
    var urls: List<String> = emptyList()
) {
    fun toImportSettings(): PastebinImportSettings {
        return PastebinImportSettings(
            pasteKeys = urls.map { PastebinUrlParser.parse(it) }
        )
    }
}