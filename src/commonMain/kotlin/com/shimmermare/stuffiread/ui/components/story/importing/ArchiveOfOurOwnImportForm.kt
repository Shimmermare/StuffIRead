package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.importer.ImportSource.ARCHIVE_OF_OUR_OWN
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImportSettings
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImportSettings.FileType
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImporter
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnUrlParser
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.input.CheckboxListField
import com.shimmermare.stuffiread.ui.theme.extendedColors

@Composable
fun ArchiveOfOurOwnImportForm(
    onImported: (ImportedStory) -> Unit
) {
    BaseSourceImportForm(
        source = ARCHIVE_OF_OUR_OWN,
        defaultData = { AOOOFormData() },
        importer = { formData ->
            val settings = ArchiveOfOurOwnImportSettings(
                workId = ArchiveOfOurOwnUrlParser.parse(formData.workUrl),
                fileTypes = formData.fileTypes
            )
            ArchiveOfOurOwnImporter.import(settings)
        },
        onImported = onImported
    ) { state ->
        TextFormField(
            id = "workUrl",
            state = state,
            name = "Work (Story) URL",
            description = "Example: https://archiveofourown.org/works/1329544354",
            getter = { it.workUrl },
            setter = { formData, value -> formData.copy(workUrl = value.trim()) },
            singleLine = true,
            maxLength = 120,
            validator = {
                if (ArchiveOfOurOwnUrlParser.matches(it)) {
                    ValidationResult.Valid
                } else {
                    ValidationResult(false, "Invalid work URL")
                }
            }
        )
        FormField(
            id = "fileTypes",
            state = state,
            name = "File types to import",
            getter = { formData -> FileType.values.associateWith { formData.fileTypes.contains(it) } },
            setter = { formData, value -> formData.copy(fileTypes = value.filterValues { it }.keys) },
        ) { value, _, onValueChange ->
            if (value.none { it.value }) {
                Text(
                    text = "If nothing is selected - no files will be imported!",
                    color = MaterialTheme.extendedColors.warning
                )
            }
            CheckboxListField(
                value = value,
                onValueChange = onValueChange
            )
        }
    }
}

private data class AOOOFormData(
    val workUrl: String = "",
    val fileTypes: Set<FileType> = emptySet()
)