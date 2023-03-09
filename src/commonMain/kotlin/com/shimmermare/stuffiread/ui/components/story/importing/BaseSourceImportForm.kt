package com.shimmermare.stuffiread.ui.components.story.importing

import ResetFormButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
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
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.components.error.ErrorInfo
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun <FormData> BaseSourceImportForm(
    source: ImportSource,
    defaultData: () -> FormData,
    importer: suspend (FormData) -> ImportedStory,
    onImported: (ImportedStory) -> Unit,
    fields: @Composable (state: InputFormState<FormData>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val state: InputFormState<FormData> = remember(source) { InputFormState(defaultData()) }
    var inProcess: Boolean by remember { mutableStateOf(false) }
    var error: ErrorInfo? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        error?.let { ErrorCard(it, modifier = Modifier.widthIn(max = 800.dp)) }

        if (inProcess) {
            Text("Importing...")
            CircularProgressIndicator()
        } else {
            InputForm(
                state = state,
                modifier = Modifier.widthIn(max = 800.dp),
                actions = { _ ->
                    ResetFormButton(
                        state = state,
                        originalData = defaultData(),
                        name = "Clear",
                    )
                    Button(
                        onClick = {
                            val formData = state.data
                            coroutineScope.launch {
                                inProcess = true
                                try {
                                    val importedStory = importer(formData)
                                    onImported(importedStory)
                                } catch (e: Exception) {
                                    Napier.e(e) { "Failed to import story from $source with form: $formData" }
                                    error = ErrorInfo(title = "Import failed", exception = e)
                                }
                                inProcess = false
                            }
                        },
                        enabled = state.isValid,
                    ) {
                        Text("Import")
                    }
                },
                fields = fields
            )
        }
    }
}