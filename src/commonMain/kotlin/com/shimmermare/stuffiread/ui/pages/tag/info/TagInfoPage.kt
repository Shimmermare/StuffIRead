package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router

object TagInfoPage : Page<TagInfoPageData> {
    override val name = "Tag"

    @Composable
    override fun renderTopBarTitle(app: AppState, data: TagInfoPageData) {
        val title by remember(data.tag.id, data.tag.name) {
            mutableStateOf("Tag - ${data.tag.name} [${data.tag.id}]")
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun renderBody(router: Router, app: AppState, data: TagInfoPageData) {
        TagInfo(router, app, data.tag)
    }
}

data class TagInfoPageData(val tag: Tag) : PageData {
    init {
        if (tag.id == 0) throw IllegalArgumentException("Can't view info for non-existing tag")
    }
}