package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryDescription
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.tags.TagDescription
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.components.form.CustomFormField
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategorySelector
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.EDIT

@Composable
fun TagForm(
    tagService: TagService,
    mode: EditTagPageMode,
    tag: Tag,
    onCancel: () -> Unit,
    onSubmit: (Tag) -> Unit
) {
    InputForm(
        value = tag,
        modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
        onCancel = onCancel,
        showResetButton = mode == EDIT,
        submitButtonText = when (mode) {
            CREATE -> "Create"
            EDIT -> "Save"
        },
        canSubmitWithoutChanges = mode == CREATE,
        onSubmit = {
            onSubmit(it)
        },
        fields = listOf(
            TextFormField(
                name = "Name",
                description = "Unique tag name. Examples: \"Mystery\", \"Greentext\"",
                getter = { it.name.value },
                setter = { form, value -> form.copy(name = TagName(value)) },
                validator = { validateName(tagService, mode, tag.id, it) }
            ),
            CustomFormField<Tag, TagCategoryId?>(
                name = "Category",
                description = "Pick category that fits this tag the most.",
                getter = { if (it.categoryId > 0) it.categoryId else 0 },
                setter = { form, value -> form.copy(categoryId = value ?: 0) },
                validator = { validateTagCategory(tagService, it ?: 0) },
                inputField = { value, onChange ->
                    TagCategorySelector(
                        tagService = tagService,
                        categoryId = value.value,
                        onSelect = onChange
                    )
                }
            ),
            TextFormField(
                name = "Description (Optional)",
                description = "Describe characteristics of this tag: when it's applicable, what it means",
                singleLine = false,
                getter = { it.description.value ?: "" },
                setter = { form, value -> form.copy(description = TagDescription.of(value.ifBlank { null })) },
                validator = ::validateDescription
            ),
            CustomFormField(
                name = "Implied tags",
                description = """Tags that are implied by this tag.
                    |Note that implied tags can form cycles. In that case all tags in cycle will be implied.
                """.trimMargin(),
                getter = { it.impliedTagIds },
                setter = { form, value -> form.copy(impliedTagIds = value) },
                validator = { validateImpliedTags(tagService, tag.id, it) },
                inputField = { value, onChange ->
                    MultiTagSelector(
                        tagService,
                        selectedIds = value.value,
                        filter = { it.id != tag.id },
                        onValueChange = onChange
                    )
                }
            ),
        )
    )
}

private fun validateName(
    tagService: TagService,
    mode: EditTagPageMode,
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
        categoryId == 0 -> "Category not selected"
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