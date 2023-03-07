package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class TagInfoPage(private val tagId: TagId) : LoadedPage<ExtendedTag>() {
    init {
        require(tagId != TagId.None) { "Can't view info for non-existing tag" }
    }

    @Composable
    override fun Title() {
        val title = remember(tagId) {
            val tag = tagService.getTagById(tagId)
            if (tag == null) {
                "Tag $tagId not found!"
            } else {
                "Tag - ${tag.name} [${tag.id}]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(): ExtendedTag {
        return tagService.getExtendedTagByIdOrThrow(tagId)
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load tag $tagId" }

        Router.goTo(
            ErrorPage(
                title = "Failed to load tag $tagId",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    Router.goTo(TagInfoPage(tagId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent() {
        TagInfo(content!!)
    }
}