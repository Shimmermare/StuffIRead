package com.shimmermare.stuffiread.ui.pages.tag.edit

import ResetFormButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryDescription
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagDescription
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.tag.MultiTagPicker
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryPicker
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.EDIT

@Composable
fun TagForm(
    mode: EditTagPageMode,
    tag: Tag,
    modifier: Modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
    onBack: () -> Unit,
    onSubmit: (Tag) -> Unit
) {
    SubmittableInputForm(
        data = tag,
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
                ResetFormButton(state, tag)
            }
        },
    ) { state ->
        TextFormField(
            id = "name",
            state = state,
            name = "Name",
            description = "Unique tag name. Examples: \"Mystery\", \"Greentext\"",
            getter = { it.name.value },
            setter = { form, value -> form.copy(name = TagName(value)) },
            validator = { validateName(tagService, mode, tag.id, it) }
        )
        FormField(
            id = "category",
            state = state,
            name = "Category",
            description = "Pick category that fits this tag the most.",
            getter = { it.categoryId },
            setter = { form, value -> form.copy(categoryId = value) },
            validator = { validateTagCategory(tagService, it) },
        ) { value, _, onChange ->
            TagCategoryPicker(
                title = "Pick tag category",
                pickedCategoryId = value,
                onPick = onChange
            )
        }
        TextFormField(
            id = "description",
            state = state,
            name = "Description (Optional)",
            description = "Describe characteristics of this tag: when it's applicable, what it means",
            singleLine = false,
            getter = { it.description.value ?: "" },
            setter = { form, value -> form.copy(description = TagDescription.of(value.ifBlank { null })) },
            validator = ::validateDescription
        )
        FormField(
            id = "impliedTags",
            state = state,
            name = "Implied tags",
            description = "Tags that are implied by this tag.\nNote that implied tags can form cycles. In that case all tags in cycle will be implied.",
            getter = { it.impliedTagIds },
            setter = { form, value -> form.copy(impliedTagIds = value) },
            validator = { validateImpliedTags(tagService, tag.id, it) },
        ) { value, _, onChange ->
            MultiTagPicker(
                title = "Pick implied tags",
                pickedTagIds = value,
                filter = { it.id != tag.id },
                onPick = onChange
            )
        }
    }
}

private fun validateName(
    tagService: TagService, mode: EditTagPageMode, currentId: TagId, name: String
): ValidationResult {
    val error = when {
        name.isBlank() -> {
            "Name can't be blank"
        }

        name.length > TagName.MAX_LENGTH -> {
            "Name length exceeded ${TagName.MAX_LENGTH} (${name.length})"
        }

        mode == CREATE && tagService.getTagByName(TagName(name)) != null -> {
            "Name is already in use"
        }

        mode == EDIT && tagService.getTagByName(TagName(name)).let { it != null && it.id != currentId } -> {
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

private fun validateTagCategory(tagService: TagService, categoryId: TagCategoryId): ValidationResult {
    val error = when {
        categoryId == TagCategoryId.None -> "Category not selected"
        tagService.getCategoryById(categoryId) == null -> "Category with ID $categoryId doesn't exist"
        else -> null
    }
    return ValidationResult(error == null, error)
}

private fun validateImpliedTags(tagService: TagService, thisTagId: TagId, impliedTags: Set<TagId>): ValidationResult {
    val error = when {
        impliedTags.contains(thisTagId) -> "Tag can't imply itself"
        !tagService.doAllTagsWithIdsExist(impliedTags) -> "Tag(s) don't exist"
        else -> null
    }
    return ValidationResult(error == null, error)
}