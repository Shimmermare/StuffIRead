package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.tagcategory.DeleteTagCategoryDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageData
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

/**
 * Page with listing of all tag categories with search available.
 */
object TagCategoriesPage : MutableTablePage<EmptyData, TagCategoryId, TagCategory>() {
    override val name = "Tag categories"

    override fun TagCategory.id(): TagCategoryId = id

    override fun TagCategory.name(): String = name.value

    override suspend fun load(app: AppState, data: EmptyData): Map<TagCategoryId, TagCategory> {
        return app.tagCategoryService.getAll().associateBy { it.id }
    }

    @Composable
    override fun LoadingError(data: EmptyData, e: Exception?) {
        Napier.e(e) { "Failed to load tag categories" }
        Text("Failed to load tag categories", style = MaterialTheme.typography.h5)
    }

    override fun getUnitName(count: Int): String {
        return if (count == 1) "tag category" else "tag categories"
    }

    override fun Router.goToCreatePage() = goTo(EditTagCategoryPage, EditTagCategoryPageData.Create)

    @Composable
    override fun DeleteDialog(app: AppState, item: TagCategory, onDeleted: () -> Unit, onDismiss: () -> Unit) {
        DeleteTagCategoryDialog(
            app.tagCategoryService,
            app.tagService,
            item,
            onConfirm = {
                app.tagCategoryService.deleteById(item.id)
                onDeleted()
            },
            onDismiss = onDismiss
        )
    }

    @Composable
    override fun TableContent(
        router: Router,
        app: AppState,
        items: Collection<TagCategory>,
        onDeleteRequest: (TagCategory) -> Unit
    ) {
        TagCategoryTable(
            categories = items,
            onClickRequest = { router.goTo(TagCategoryInfoPage, TagCategoryInfoPageData(it.id)) },
            onEditRequest = { router.goTo(EditTagCategoryPage, EditTagCategoryPageData(it)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}
