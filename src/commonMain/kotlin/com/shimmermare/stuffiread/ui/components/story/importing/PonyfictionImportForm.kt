package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.importer.ImportSource.PONYFICTION
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.ponyfiction.PonyfictionImportSettings
import com.shimmermare.stuffiread.importer.ponyfiction.PonyfictionImporter
import com.shimmermare.stuffiread.importer.ponyfiction.PonyfictionUrlParser
import com.shimmermare.stuffiread.ui.components.form.LeanBoolFormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun PonyfictionImportForm(
    onImported: (ImportedStory) -> Unit
) {
    BaseSourceImportForm(
        source = PONYFICTION,
        defaultData = { PonyfictionFormData() },
        importer = { formData ->
            val settings = PonyfictionImportSettings(
                storyId = PonyfictionUrlParser.parse(formData.storyUrl),
                downloadFb2 = formData.downloadFb2,
                downloadTxt = formData.downloadTxt,
            )
            PonyfictionImporter.import(settings)
        },
        onImported = onImported
    ) { state ->
        TextFormField(
            id = "storyUrl",
            state = state,
            name = Strings.components_importing_ponyfictionImportForm_storyUrl.remember(),
            description = Strings.components_importing_ponyfictionImportForm_storyUrl_description
                .remember("https://ponyfiction.org/story/1321"),
            getter = { it.storyUrl },
            setter = { formData, value -> formData.copy(storyUrl = value.trim()) },
            singleLine = true,
            maxLength = 120,
            validator = {
                if (PonyfictionUrlParser.matches(it)) {
                    ValidationResult.Valid
                } else {
                    ValidationResult(false, Strings.components_importing_ponyfictionImportForm_invalid())
                }
            }
        )
        LeanBoolFormField(
            id = "downloadFb2",
            state = state,
            name = Strings.components_importing_ponyfictionImportForm_downloadType.remember(".fb2"),
            getter = { it.downloadFb2 },
            setter = { formData, value -> formData.copy(downloadFb2 = value) },
        )
        LeanBoolFormField(
            id = "downloadTxt",
            state = state,
            name = Strings.components_importing_ponyfictionImportForm_downloadType.remember(".txt"),
            getter = { it.downloadTxt },
            setter = { formData, value -> formData.copy(downloadTxt = value) },
        )
    }
}

private data class PonyfictionFormData(
    val storyUrl: String = "",
    val downloadFb2: Boolean = true,
    val downloadTxt: Boolean = false
)