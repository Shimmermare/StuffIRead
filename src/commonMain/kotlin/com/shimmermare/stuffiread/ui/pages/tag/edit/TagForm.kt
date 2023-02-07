package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.domain.tags.*
import com.shimmermare.stuffiread.ui.components.form.CustomFormField
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategorySelector
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.EDIT
import java.time.OffsetDateTime

@Composable
fun TagForm(
    tagCategoryService: TagCategoryService,
    tagService: TagService,
    mode: EditTagPageMode,
    tag: Tag,
    onCancel: () -> Unit,
    onSubmit: (Tag) -> Unit
) {
    InputForm(
        value = tag,
        modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
        showResetButton = mode == EDIT,
        onCancel = onCancel,
        submitButtonText = when (mode) {
            CREATE -> "Create"
            EDIT -> "Save"
        },
        canSubmitWithoutChanges = mode == CREATE,
        onSubmit = {
            onSubmit(
                when (mode) {
                    CREATE -> it.copy(created = OffsetDateTime.now(), updated = OffsetDateTime.now())
                    EDIT -> it.copy(updated = OffsetDateTime.now())
                }
            )
        },
        fields = listOf(
            TextFormField(
                name = "Name",
                description = "Unique tag name. Examples: \"Mystery\", \"Greentext\"",
                getter = { it.name },
                setter = { form, value -> form.copy(name = value) },
                validator = { validateName(tagService, mode, tag.id, it) }
            ),
            CustomFormField<Tag, TagCategoryId?>(
                name = "Category",
                description = "Pick category that fits this tag the most.",
                getter = { if (it.categoryId > 0) it.categoryId else 0 },
                setter = { form, value -> form.copy(categoryId = value ?: 0) },
                validator = { validateTagCategory(tagCategoryService, it ?: 0) },
                inputField = { value, onChange ->
                    TagCategorySelector(
                        tagCategoryService = tagCategoryService,
                        categoryId = value.value,
                        onSelect = onChange
                    )
                }
            ),
            TextFormField(
                name = "Description (Optional)",
                description = "Describe characteristics of this tag: when it's applicable, what it means",
                singleLine = false,
                getter = { it.description ?: "" },
                setter = { form, value -> form.copy(description = value.ifBlank { null }) },
                validator = ::validateDescription
            ),
            CustomFormField(
                name = "Implied tags",
                description = """Tags that are implied by this tag.
                    |Note that implied tags can form cycles. In that case all tags in cycle will be implied.
                """.trimMargin(),
                getter = { it.impliedTags },
                setter = { form, value -> form.copy(impliedTags = value) },
                validator = { validateImpliedTags(tagService, tag.id, it) },
                inputField = { value, onChange ->
                    MultiTagSelector(
                        tagCategoryService,
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

        name.length > TagCategory.MAX_NAME_LENGTH -> {
            "Name length exceeded ${TagCategory.MAX_NAME_LENGTH} (${name.length})"
        }

        mode == CREATE && tagService.getIdByName(name) != null -> {
            "Name is already in use"
        }

        mode == EDIT && tagService.getIdByName(name).let { it != null && it != currentId } -> {
            "Name is already in use"
        }

        else -> null
    }
    return ValidationResult(error == null, error)
}

private fun validateDescription(description: String): ValidationResult {
    val error = when {
        description.length > TagCategory.MAX_DESCRIPTION_LENGTH -> {
            "Description length exceeded ${TagCategory.MAX_DESCRIPTION_LENGTH} (${description.length})"
        }

        else -> null
    }
    return ValidationResult(error == null, error)
}

private fun validateTagCategory(tagCategoryService: TagCategoryService, categoryId: TagCategoryId): ValidationResult {
    val error = when {
        categoryId == 0 -> "Category not selected"
        !tagCategoryService.existsById(categoryId) -> "Category with ID $categoryId doesn't exist"
        else -> null
    }
    return ValidationResult(error == null, error)
}

private fun validateImpliedTags(tagService: TagService, thisTagId: TagId, impliedTags: Set<TagId>): ValidationResult {
    val error = when {
        impliedTags.contains(thisTagId) -> "Tag can't imply itself"
        !tagService.allExistByIds(impliedTags) -> "Tag(s) don't exist"
        else -> null
    }
    return ValidationResult(error == null, error)
}