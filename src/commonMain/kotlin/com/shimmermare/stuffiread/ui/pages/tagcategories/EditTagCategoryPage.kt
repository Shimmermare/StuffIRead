package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryForm
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.util.remember

class EditTagCategoryPage private constructor(
    private val creationMode: Boolean,
    private val editingCategoryId: TagCategoryId = TagCategoryId.None,
    private val prefillWith: TagCategory,
) : Page {

    @Composable
    override fun Title() {
        val title = if (creationMode) {
            Strings.page_tagCategoryEdit_title_create.remember()
        } else {
            val category = tagService.getCategoryByIdOrThrow(editingCategoryId)
            Strings.page_tagCategoryEdit_title_edit(category.name) + " [" + editingCategoryId + "]"
        }
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body() {
        AnimatedFadeIn {
            TagCategoryForm(
                creationMode = creationMode,
                category = prefillWith,
                onBack = {
                    Router.goTo(if (creationMode) TagCategoriesPage() else TagCategoryInfoPage(editingCategoryId))
                },
                onSubmit = {
                    val category = if (creationMode) tagService.createCategory(it) else tagService.updateCategory(it)
                    Router.goTo(TagCategoryInfoPage(category.id))
                }
            )
        }
    }

    companion object {
        fun create() = EditTagCategoryPage(
            creationMode = true,
            prefillWith = TagCategory(
                name = TagCategoryName(Strings.components_tagCategoryForm_name_default_new()),
                color = TagCategory.DEFAULT_COLOR,
            )
        )

        fun createCopy(original: TagCategory) = EditTagCategoryPage(
            creationMode = true,
            prefillWith = original.copy(
                id = TagCategoryId.None,
                name = TagCategoryName(Strings.components_tagCategoryForm_name_default_copy(original.name))
            )
        )

        fun edit(category: TagCategory): EditTagCategoryPage {
            if (category.id == TagCategoryId.None) throw IllegalArgumentException("Can edit only existing category")
            return EditTagCategoryPage(
                creationMode = false,
                editingCategoryId = category.id,
                prefillWith = category
            )
        }
    }
}