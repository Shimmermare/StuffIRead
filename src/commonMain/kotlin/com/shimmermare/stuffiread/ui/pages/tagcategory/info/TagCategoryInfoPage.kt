package com.shimmermare.stuffiread.ui.pages.tagcategory.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Display tag category properties and related stats.
 */
object TagCategoryInfoPage : Page<TagCategoryInfoPageData> {
    override val name = "Tag category"

    @Composable
    override fun renderTopBarTitle(app: AppState, data: TagCategoryInfoPageData) {
        val title by remember(data.category.id, data.category.name) {
            mutableStateOf("Tag category - ${data.category.name} [${data.category.id}]")
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun renderBody(router: Router, app: AppState, data: TagCategoryInfoPageData) {
        TagCategoryInfo(router, app, data.category)
    }
}

data class TagCategoryInfoPageData(val category: TagCategory) : PageData {
    init {
        if (category.id == 0) throw IllegalArgumentException("Can't view info for non-existing tag category")
    }
}