package com.shimmermare.stuffiread.ui.pages.tagcategory.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode.EDIT
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router
import java.time.OffsetDateTime

object EditTagCategoryPage : Page<EditTagCategoryPageData> {
    override val name = "Tag category"

    @Composable
    override fun Title(app: AppState, data: EditTagCategoryPageData) {
        val title by remember(data.mode, data.category.id, data.category.name) {
            mutableStateOf(
                when (data.mode) {
                    CREATE -> "New tag category"
                    EDIT -> "Tag category (Editing) - ${data.category.name} [${data.category.id}]"
                }
            )
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(router: Router, app: AppState, data: EditTagCategoryPageData) {
        AnimatedFadeIn {
            TagCategoryForm(
                tagCategoryService = app.tagCategoryService,
                mode = data.mode,
                category = data.category,
                onCancel = {
                    when (data.mode) {
                        CREATE -> router.goTo(TagCategoriesPage, EmptyData)
                        EDIT -> router.goTo(TagCategoryInfoPage, TagCategoryInfoPageData(data.category.id))
                    }
                },
                onSubmit = {
                    val category = app.tagCategoryService.createOrUpdate(it)
                    router.goTo(TagCategoryInfoPage, TagCategoryInfoPageData(category.id))
                }
            )
        }
    }
}

data class EditTagCategoryPageData(
    val mode: EditTagCategoryPageMode,
    val category: TagCategory,
) : PageData {
    constructor(tagCategory: TagCategory) : this(
        mode = EDIT,
        category = tagCategory
    ) {
        if (tagCategory.id == 0) throw IllegalArgumentException("Can edit only existing tag category")
    }

    companion object {
        val Create = EditTagCategoryPageData(
            mode = CREATE,
            category = TagCategory(
                name = TagCategoryName("New tag category"),
                color = TagCategory.DEFAULT_COLOR,
                created = OffsetDateTime.MIN
            )
        )

        fun createCopy(original: TagCategory) = EditTagCategoryPageData(
            mode = CREATE,
            category = original.copy(
                id = 0,
                name = TagCategoryName("Copy of " + original.name)
            )
        )
    }
}

enum class EditTagCategoryPageMode {
    CREATE, EDIT
}