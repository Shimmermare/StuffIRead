package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.ExtendedTag
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

object TagInfoPage : LoadedPage<TagInfoPageData, ExtendedTag>() {
    override val name = "Tag"

    @Composable
    override fun Title(app: AppState, data: TagInfoPageData) {
        val title = remember(data.tagId) {
            val tag = app.tagService.getById(data.tagId)
            if (tag == null) {
                "Tag ${data.tagId} not found!"
            } else {
                "Tag - ${tag.name} [${tag.id}]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState, data: TagInfoPageData): ExtendedTag {
        return app.tagService.getExtendedByIdOrThrow(data.tagId)
    }

    @Composable
    override fun LoadingError(data: TagInfoPageData, e: Exception?) {
        Napier.e(e) { "Failed to load tag ${data.tagId}" }
        Text("Failed to load tag ${data.tagId}", style = MaterialTheme.typography.h5)
    }

    @Composable
    override fun LoadedContent(router: Router, app: AppState, loaded: ExtendedTag) {
        TagInfo(router, app, loaded)
    }
}

data class TagInfoPageData(val tagId: TagId) : PageData {
    init {
        require(tagId != 0) { "Can't view info for non-existing tag" }
    }
}