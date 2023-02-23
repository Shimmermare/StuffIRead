package com.shimmermare.stuffiread.ui.pages.tagcategory.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import com.shimmermare.stuffiread.ui.components.colorpicker.PopupColorPicker
import com.shimmermare.stuffiread.ui.components.form.CustomFormField
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.IntFormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.EDIT

@Composable
fun TagCategoryForm(
    tagService: TagService,
    mode: EditTagCategoryPageMode,
    category: TagCategory,
    onCancel: () -> Unit,
    onSubmit: (TagCategory) -> Unit
) {
    InputForm(
        value = category,
        modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
        onCancel = onCancel,
        showResetButton = mode == EDIT,
        submitButtonText = when (mode) {
            CREATE -> "Create"
            EDIT -> "Save"
        },
        canSubmitWithoutChanges = mode == CREATE,
        onSubmit = onSubmit,
        fields = listOf(
            TextFormField(
                name = "Name",
                description = "Unique tag category name. Examples: \"Characters\", \"Ships\"",
                getter = { it.name.value },
                setter = { form, value -> form.copy(name = TagCategoryName(value)) },
                validator = { validateName(tagService, mode, category.id, it) }
            ),
            TextFormField(
                name = "Description (Optional)",
                description = "Describe characteristics of this tag category: what tags should be here and why.",
                singleLine = false,
                getter = { it.description.value ?: "" },
                setter = { form, value -> form.copy(description = TagCategoryDescription.of(value.ifBlank { null })) },
                validator = ::validateDescription
            ),
            IntFormField(
                name = "Sort Order",
                description = "Order in which tags are displayed on story.",
                getter = { it.sortOrder },
                setter = { form, value -> form.copy(sortOrder = value) },
                range = 0..Int.MAX_VALUE
            ),
            CustomFormField(
                name = "Color",
                description = "Color of tags in this category.",
                getter = { Color(it.color) },
                setter = { form, value -> form.copy(color = value.toArgb()) }
            )
            { value, onValueChange ->
                PopupColorPicker(
                    color = value.value,
                    button = { FilledNameText("Pick color", it) },
                    onPick = { onValueChange(it) }
                )
            }
        )
    )
}

private fun validateName(
    tagService: TagService,
    mode: EditTagCategoryPageMode,
    currentId: TagCategoryId,
    name: String
): ValidationResult {
    val error = when {
        name.isBlank() -> {
            "Name can't be blank"
        }

        name.length > TagCategoryName.MAX_LENGTH -> {
            "Name length exceeded ${TagCategoryName.MAX_LENGTH} (${name.length})"
        }

        mode == CREATE && tagService.getCategoryByName(TagCategoryName(name)) != null -> {
            "Name is already in use"
        }

        mode == EDIT && tagService.getCategoryByName(TagCategoryName(name))
            .let { it != null && it.id != currentId } -> {
            "Name is already in use"
        }

        else -> null
    }
    return ValidationResult(error == null, error)
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