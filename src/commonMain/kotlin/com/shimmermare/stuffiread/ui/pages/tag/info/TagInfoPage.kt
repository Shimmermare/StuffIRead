package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class TagInfoPage(private val tagId: TagId) : LoadedPage<ExtendedTag>() {
    init {
        require(tagId != 0) { "Can't view info for non-existing tag" }
    }

    @Composable
    override fun Title(app: AppState) {
        val title = remember(tagId) {
            val tag = app.storyArchive!!.tagService.getTagById(tagId)
            if (tag == null) {
                "Tag $tagId not found!"
            } else {
                "Tag - ${tag.name} [${tag.id}]"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState): ExtendedTag {
        return app.storyArchive!!.tagService.getExtendedTagByIdOrThrow(tagId)
    }

    @Composable
    override fun LoadingError(app: AppState) {
        Napier.e(error) { "Failed to load tag $tagId" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load tag $tagId",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    app.router.goTo(TagInfoPage(tagId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        TagInfo(app, content!!)
    }
}