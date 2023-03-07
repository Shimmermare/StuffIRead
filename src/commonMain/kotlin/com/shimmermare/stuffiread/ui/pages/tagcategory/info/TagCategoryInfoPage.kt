package com.shimmermare.stuffiread.ui.pages.tagcategory.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
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
        val title = remember(categoryId) {
            val category = tagService.getCategoryById(categoryId)
            if (category == null) {
                "Tag category $categoryId not found!"
            } else {
                "Tag category - ${category.name} [${category.id}]"
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
                title = "Failed to load tag category $categoryId",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
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