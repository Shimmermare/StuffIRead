package com.shimmermare.stuffiread.ui.pages.tagcategory.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

/**
 * Display tag category properties and related stats.
 */
class TagCategoryInfoPage(private val categoryId: TagCategoryId) : LoadedPage<TagCategory>() {
    init {
        require(categoryId != 0) { "Can't view info for non-existing tag category" }
    }

    @Composable
    override fun Title(app: AppState) {
        val title = remember(categoryId) {
            val category = app.storyArchive!!.tagService.getCategoryById(categoryId)
            if (category == null) {
                "Tag category $categoryId not found!"
            } else {
                "Tag category - ${category.name} [${category.id}]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState): TagCategory {
        return app.storyArchive!!.tagService.getCategoryByIdOrThrow(categoryId)
    }

    @Composable
    override fun LoadingError(app: AppState) {
        Napier.e(error) { "Failed to load tag category $categoryId" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load tag category $categoryId",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    app.router.goTo(TagCategoryInfoPage(categoryId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        TagCategoryInfo(app, content!!)
    }
}