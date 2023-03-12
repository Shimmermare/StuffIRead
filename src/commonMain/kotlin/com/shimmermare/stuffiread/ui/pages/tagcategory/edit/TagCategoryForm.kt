package com.shimmermare.stuffiread.ui.pages.tagcategory.edit

import ResetFormButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryDescription
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.RangedIntFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.input.PopupColorPicker
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.EDIT

@Composable
fun TagCategoryForm(
    mode: EditTagCategoryPageMode,
    category: TagCategory,
    modifier: Modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
    onBack: () -> Unit,
    onSubmit: (TagCategory) -> Unit
) {
    SubmittableInputForm(
        data = category,
        modifier = modifier,
        submitButtonText = when (mode) {
            CREATE -> "Create"
            EDIT -> "Save"
        },
        canSubmitWithoutChanges = mode == CREATE,
        onSubmit = onSubmit,
        actions = { state ->
            Button(onClick = onBack) {
                Text("Back")
            }
            if (mode == EDIT) {
                ResetFormButton(state, category)
            }
        },
    ) { state ->
        TextFormField(
            id = "name",
            state = state,
            name = "Name",
            description = "Unique tag category name. Examples: \"Characters\", \"Ships\"",
            getter = { it.name.value },
            setter = { form, value -> form.copy(name = TagCategoryName(value)) },
            validator = { validateName(tagService, mode, category.id, it) },
        )
        TextFormField(
            id = "description",
            state = state,
            name = "Description (Optional)",
            description = "Describe characteristics of this tag category: what tags should be here and why.",
            getter = { it.description.value ?: "" },
            setter = { form, value -> form.copy(description = TagCategoryDescription.of(value.ifBlank { null })) },
            validator = ::validateDescription,
            singleLine = false
        )
        RangedIntFormField(
            id = "sortOrder",
            state = state,
            name = "Sort Order",
            description = "Order in which tags are displayed on story.",
            getter = { it.sortOrder },
            setter = { form, value -> form.copy(sortOrder = value) },
        )
        FormField(
            id = "color",
            state = state,
            name = "Color",
            description = "Color of tags in this category.",
            getter = { Color(it.color) },
            setter = { form, value -> form.copy(color = value.toArgb()) }
        ) { value, _, onValueChange ->
            PopupColorPicker(
                color = value,
                button = { FilledNameText("Pick color", it) },
                onPick = { onValueChange(it) }
            )
        }
    }
}

private fun validateName(
    tagService: TagService,
    mode: EditTagCategoryPageMode,
    currentId: TagCategoryId,
    name: String
): ValidationResult {
    val validName = try {
        TagCategoryName(name)
    } catch (e: IllegalArgumentException) {
        return ValidationResult(false, e.message)
    }

    val existing = tagService.getCategoryByName(validName)
    if (mode == CREATE || (mode == EDIT && existing?.takeIf { it.id != currentId } != null)) {
        return ValidationResult(false, "Name is already in use")
    }

    return ValidationResult.Valid
}

private fun validateDescription(description: String): ValidationResult {
    val error = when {
        description.length > TagCategoryDescription.MAX_LENGTH -> {
            "Description length exceeded ${TagCategoryDescription.MAX_LENGTH} (${description.length})"
        }

        else -> null
    }
    return ValidationResult(error == null, error)
}