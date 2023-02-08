package com.shimmermare.stuffiread.ui.pages.tagcategory.info

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

/**
 * Display tag category properties and related stats.
 */
object TagCategoryInfoPage : LoadedPage<TagCategoryInfoPageData, TagCategory>() {
    override val name = "Tag category"

    @Composable
    override fun Title(app: AppState, data: TagCategoryInfoPageData) {
        val title = remember(data.categoryId) {
            val category = app.tagCategoryService.getById(data.categoryId)
            if (category == null) {
                "Tag category ${data.categoryId} not found!"
            } else {
                "Tag category - ${category.name} [${category.id}]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState, data: TagCategoryInfoPageData): TagCategory {
        return app.tagCategoryService.getByIdOrThrow(data.categoryId)
    }

    @Composable
    override fun LoadingError(data: TagCategoryInfoPageData, e: Exception?) {
        Napier.e(e) { "Failed to load tag categories" }
        Text("Failed to load tag categories", style = MaterialTheme.typography.h5)
    }

    @Composable
    override fun LoadedContent(router: Router, app: AppState, loaded: TagCategory) {
        TagCategoryInfo(router, app, loaded)
    }
}

data class TagCategoryInfoPageData(val categoryId: TagCategoryId) : PageData {
    init {
        require(categoryId != 0) { "Can't view info for non-existing tag category" }
    }
}