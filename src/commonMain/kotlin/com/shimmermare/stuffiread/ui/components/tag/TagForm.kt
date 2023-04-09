package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagDescription
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.form.BackFormButton
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.ResetFormButton
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryPicker
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun TagForm(
    creationMode: Boolean,
    tag: Tag,
    modifier: Modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
    onBack: () -> Unit,
    onSubmit: (Tag) -> Unit
) {
    SubmittableInputForm(
        data = tag,
        modifier = modifier,
        submitButtonText = if (creationMode) {
            Strings.components_tagForm_submitButton_create.remember()
        } else {
            Strings.components_tagForm_submitButton_edit.remember()
        },
        canSubmitWithoutChanges = creationMode,
        onSubmit = onSubmit,
        actions = { state ->
            BackFormButton(onBack)
            if (!creationMode) {
                ResetFormButton(state, tag)
            }
        },
    ) { state ->
        TextFormField(
            id = "name",
            state = state,
            name = Strings.components_tagForm_name.remember(),
            description = Strings.components_tagForm_name_description.remember(),
            getter = { it.name.value },
            setter = { form, value -> form.copy(name = TagName(value)) },
            validator = { validateName(tagService, creationMode, tag.id, it) },
            maxLength = TagName.MAX_LENGTH,
        )
        FormField(
            id = "category",
            state = state,
            name = Strings.components_tagForm_category.remember(),
            getter = { it.categoryId },
            setter = { form, value -> form.copy(categoryId = value) },
            validator = { validateTagCategory(it) },
        ) { value, _, onChange ->
            TagCategoryPicker(
                title = Strings.components_tagForm_category_pickerTitle.remember(),
                pickedCategoryId = value,
                onPick = { onChange(it) }
            )
        }
        TextFormField(
            id = "description",
            state = state,
            name = Strings.components_tagForm_description.remember(),
            description = Strings.components_tagForm_description_description.remember(),
            singleLine = false,
            getter = { it.description.value ?: "" },
            setter = { form, value -> form.copy(description = TagDescription.of(value.ifBlank { null })) },
            validator = { ValidationResult.fromException { TagDescription.of(it) } },
            maxLength = TagDescription.MAX_LENGTH,
        )
        FormField(
            id = "impliedTags",
            state = state,
            name = Strings.components_tagForm_impliedTags.remember(),
            description = Strings.components_tagForm_impliedTags_description.remember(),
            getter = { it.impliedTagIds },
            setter = { form, value -> form.copy(impliedTagIds = value) },
            validator = { validateImpliedTags(tag.id, it) },
        ) { value, _, onChange ->
            MultiTagPicker(
                title = Strings.components_tagForm_impliedTags_pickerTitle.remember(),
                pickedTagIds = value,
                filter = { it.id != tag.id },
                onPick = onChange
            )
        }
    }
}

private fun validateName(
    tagService: TagService,
    creationMode: Boolean,
    currentId: TagId,
    name: String
): ValidationResult {
    if (name.isBlank()) {
        return ValidationResult(false, Strings.components_tagForm_name_invalid_blank())
    }
    if (name.startsWith(' ') || name.endsWith(' ')) {
        return ValidationResult(false, Strings.components_tagForm_name_invalid_whitespace())
    }

    val tagName = try {
        TagName(name)
    } catch (e: IllegalArgumentException) {
        return ValidationResult(false, e.message)
    }
    val existing = tagService.getTagByName(tagName)

    val nameTaken = if (creationMode) {
        existing != null
    } else {
        existing?.takeIf { it.id != currentId } != null
    }
    if (nameTaken) {
        return ValidationResult(false, Strings.components_tagForm_name_invalid_taken())
    }

    return ValidationResult.Valid
}

private fun validateTagCategory(categoryId: TagCategoryId): ValidationResult {
    val error = when (categoryId) {
        TagCategoryId.None -> Strings.components_tagForm_category_invalid_notSelected()
        else -> null
    }
    return ValidationResult(error == null, error)
}

private fun validateImpliedTags(thisTagId: TagId, impliedTags: Set<TagId>): ValidationResult {
    val error = when {
        impliedTags.contains(thisTagId) -> Strings.components_tagForm_impliedTags_invalid_self()
        else -> null
    }
    return ValidationResult(error == null, error)
}