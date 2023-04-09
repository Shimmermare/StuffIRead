package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.tagcategory.DeleteTagCategoryDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
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
                title = Strings.page_tagCategories_failedToLoad_title.remember(),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_tagCategories_failedToLoad_tryAgainButton.remember()) {
                    Router.goTo(TagCategoriesPage())
                })
            )
        )
    }

    override fun getSearchResultText() = tagCategories_search_result

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
            onEditRequest = { Router.goTo(EditTagCategoryPage.edit(it)) },
            onDeleteRequest = onDeleteRequest
        )
    }

    companion object {
        private val tagCategories_search_result = PluralLocalizedString(
            Strings.page_tagCategories_search_result_zero,
            Strings.page_tagCategories_search_result_one,
            Strings.page_tagCategories_search_result_two,
            Strings.page_tagCategories_search_result_few,
            Strings.page_tagCategories_search_result_many,
            Strings.page_tagCategories_search_result_other,
        )
    }
}
