package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
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

    override suspend fun load(app: AppState): Map<TagCategoryId, TagCategory> {
        return app.storyArchive!!.tagService.getCategories().associateBy { it.id }
    }

    @Composable
    override fun LoadingError(app: AppState) {
        Napier.e(error) { "Failed to load tag categories" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load tag categories",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    app.router.goTo(TagCategoriesPage())
                })
            )
        )
    }

    override fun getUnitName(count: Int): String {
        return if (count == 1) "tag category" else "tag categories"
    }

    override fun Router.goToCreatePage() = goTo(EditTagCategoryPage.create())

    @Composable
    override fun DeleteDialog(app: AppState, item: TagCategory, onDeleted: () -> Unit, onDismiss: () -> Unit) {
        DeleteTagCategoryDialog(
            app.storyArchive!!.tagService,
            item,
            onDeleted,
            onDismiss
        )
    }

    @Composable
    override fun TableContent(
        app: AppState,
        items: Collection<TagCategory>,
        onDeleteRequest: (TagCategory) -> Unit
    ) {
        TagCategoryTable(
            categories = items,
            onClickRequest = { app.router.goTo(TagCategoryInfoPage(it.id)) },
            onEditRequest = { app.router.goTo(EditTagCategoryPage(it)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}
