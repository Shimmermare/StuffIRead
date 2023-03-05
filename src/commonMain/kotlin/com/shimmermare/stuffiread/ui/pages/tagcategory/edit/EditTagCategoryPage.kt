package com.shimmermare.stuffiread.ui.pages.tagcategory.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.EDIT
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.routing.Page

class EditTagCategoryPage(
    private val mode: EditTagCategoryPageMode,
    private val category: TagCategory,
) : Page {
    constructor(tagCategory: TagCategory) : this(
        mode = EDIT,
        category = tagCategory
    ) {
        if (tagCategory.id == TagCategoryId.None) throw IllegalArgumentException("Can edit only existing tag category")
    }

    @Composable
    override fun Title(app: AppState) {
        val title by remember(mode, category.id, category.name) {
            mutableStateOf(
                when (mode) {
                    CREATE -> "New tag category"
                    EDIT -> "Tag category (Editing) - ${category.name} [${category.id}]"
                }
            )
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(app: AppState) {
        AnimatedFadeIn {
            TagCategoryForm(
                tagService = app.storyArchive!!.tagService,
                mode = mode,
                category = category,
                onBack = {
                    when (mode) {
                        CREATE -> app.router.goTo(TagCategoriesPage())
                        EDIT -> app.router.goTo(TagCategoryInfoPage(category.id))
                    }
                },
                onSubmit = {
                    val category = when (mode) {
                        CREATE -> app.storyArchive!!.tagService.createCategory(it)
                        EDIT -> app.storyArchive!!.tagService.updateCategory(it)
                    }
                    app.router.goTo(TagCategoryInfoPage(category.id))
                }
            )
        }
    }

    companion object {
        fun create() = EditTagCategoryPage(
            mode = CREATE,
            category = TagCategory(
                name = TagCategoryName("New tag category"),
                color = TagCategory.DEFAULT_COLOR,
            )
        )

        fun createCopy(original: TagCategory) = EditTagCategoryPage(
            mode = CREATE,
            category = original.copy(
                id = TagCategoryId.None,
                name = TagCategoryName("Copy of " + original.name)
            )
        )
    }
}

enum class EditTagCategoryPageMode {
    CREATE, EDIT
}