package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.tagcategory.DeleteTagCategoryDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

/**
 * Page with listing of all tag categories with search available.
 */
class TagCategoriesPage : MutableTablePage<TagCategoryId, TagCategory>() {
    override fun TagCategory.id(): TagCategoryId = id

    override fun TagCategory.name(): String = name.value

    override suspend fun load(): Map<TagCategoryId, TagCategory> {
        return tagService.getCategories().associateBy { it.id }
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load tag categories" }

        Router.goTo(
            ErrorPage(
                title = "Failed to load tag categories",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    Router.goTo(TagCategoriesPage())
                })
            )
        )
    }

    override fun getUnitName(count: Int): String {
        return if (count == 1) "tag category" else "tag categories"
    }

    override fun Router.goToCreatePage() = goTo(EditTagCategoryPage.create())

    @Composable
    override fun DeleteDialog(item: TagCategory, onDeleted: () -> Unit, onDismissRequest: () -> Unit) {
        DeleteTagCategoryDialog(
            item,
            onDeleted,
            onDismissRequest
        )
    }

    @Composable
    override fun TableContent(
        items: Collection<TagCategory>,
        onDeleteRequest: (TagCategory) -> Unit
    ) {
        TagCategoryTable(
            categories = items,
            onClickRequest = { Router.goTo(TagCategoryInfoPage(it.id)) },
            onEditRequest = { Router.goTo(EditTagCategoryPage(it)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}
