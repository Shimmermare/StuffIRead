package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.CurrentLocale
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryInfo
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

/**
 * Display tag category properties and related stats.
 */
class TagCategoryInfoPage(private val categoryId: TagCategoryId) : LoadedPage<TagCategory>() {
    init {
        require(categoryId != TagCategoryId.None) { "Can't view info for non-existing tag category" }
    }

    @Composable
    override fun Title() {
        val title = remember(categoryId, CurrentLocale) {
            val category = tagService.getCategoryById(categoryId)
            if (category == null) {
                Strings.page_tagCategoryInfo_error_notFound(categoryId)
            } else {
                Strings.page_tagCategoryInfo_title(category.name) + " [" + categoryId + "]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(): TagCategory {
        return tagService.getCategoryByIdOrThrow(categoryId)
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load tag category $categoryId" }

        Router.goTo(
            ErrorPage(
                title = Strings.page_tagCategoryInfo_error_failedToLoad(categoryId),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_tagCategoryInfo_error_failedToLoad_tryAgainButton.toString()) {
                    Router.goTo(TagCategoryInfoPage(categoryId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent() {
        TagCategoryInfo(content!!)
    }
}