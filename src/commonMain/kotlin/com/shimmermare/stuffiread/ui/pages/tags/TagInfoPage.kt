package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.CurrentLocale
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.tag.TagInfo
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class TagInfoPage(private val tagId: TagId) : LoadedPage<ExtendedTag>() {
    init {
        require(tagId != TagId.None) { "Can't view info for non-existing tag" }
    }

    @Composable
    override fun Title() {
        val title = remember(tagId, CurrentLocale) {
            val tag = tagService.getTagById(tagId)
            if (tag == null) {
                Strings.page_tagInfo_error_notFound(tagId)
            } else {
                Strings.page_tagInfo_title(tag.name) + " [" + tagId + "]"
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
                title = Strings.page_tagInfo_error_failedToLoad(tagId),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_tagInfo_error_failedToLoad_tryAgainButton()) {
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