package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryDescription
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.form.BackFormButton
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.RangedIntFormField
import com.shimmermare.stuffiread.ui.components.form.ResetFormButton
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.input.PopupColorPicker
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun TagCategoryForm(
    creationMode: Boolean,
    category: TagCategory,
    modifier: Modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
    onBack: () -> Unit,
    onSubmit: (TagCategory) -> Unit
) {
    SubmittableInputForm(
        data = category,
        modifier = modifier,
        submitButtonText = if (creationMode) {
            Strings.components_tagCategoryForm_submitButton_create.remember()
        } else {
            Strings.components_tagCategoryForm_submitButton_edit.remember()
        },
        canSubmitWithoutChanges = creationMode,
        onSubmit = onSubmit,
        actions = { state ->
            BackFormButton(onBack)
            if (!creationMode) {
                ResetFormButton(state, category)
            }
        },
    ) { state ->
        TextFormField(
            id = "name",
            state = state,
            name = Strings.components_tagCategoryForm_name.remember(),
            description = Strings.components_tagCategoryForm_name_description.remember(),
            getter = { it.name.value },
            setter = { form, value -> form.copy(name = TagCategoryName(value)) },
            validator = { validateName(tagService, creationMode, category.id, it) },
        )
        TextFormField(
            id = "description",
            state = state,
            name = Strings.components_tagCategoryForm_description.remember(),
            description = Strings.components_tagCategoryForm_description_description.remember(),
            getter = { it.description.value ?: "" },
            setter = { form, value -> form.copy(description = TagCategoryDescription.of(value.ifBlank { null })) },
            validator = { ValidationResult.fromException { TagCategoryDescription.of(it) } },
            singleLine = false
        )
        RangedIntFormField(
            id = "sortOrder",
            state = state,
            name = Strings.components_tagCategoryForm_sortingOrder.remember(),
            description = Strings.components_tagCategoryForm_sortingOrder_description.remember(),
            getter = { it.sortOrder },
            setter = { form, value -> form.copy(sortOrder = value) },
        )
        FormField(id = "color",
            state = state,
            name = Strings.components_tagCategoryForm_color.remember(),
            description = Strings.components_tagCategoryForm_color_description.remember(),
            getter = { Color(it.color) },
            setter = { form, value -> form.copy(color = value.toArgb()) }) { value, _, onValueChange ->
            PopupColorPicker(color = value,
                button = { FilledNameText(Strings.components_tagCategoryForm_color_pickButton.remember(), it) },
                onPick = { onValueChange(it) })
        }
    }
}

private fun validateName(
    tagService: TagService, creationMode: Boolean, currentId: TagCategoryId, name: String
): ValidationResult {
    if (name.isBlank()) {
        return ValidationResult(false, Strings.components_tagCategoryForm_name_invalid_blank())
    }
    if (name.startsWith(' ') || name.endsWith(' ')) {
        return ValidationResult(false, Strings.components_tagCategoryForm_name_invalid_whitespace())
    }

    val categoryName = try {
        TagCategoryName(name)
    } catch (e: IllegalArgumentException) {
        return ValidationResult(false, e.message)
    }
    val existing = tagService.getCategoryByName(categoryName)

    val nameTaken = if (creationMode) {
        existing != null
    } else {
        existing?.takeIf { it.id != currentId } != null
    }
    if (nameTaken) {
        return ValidationResult(false, Strings.components_tagCategoryForm_name_invalid_taken())
    }

    return ValidationResult.Valid
}