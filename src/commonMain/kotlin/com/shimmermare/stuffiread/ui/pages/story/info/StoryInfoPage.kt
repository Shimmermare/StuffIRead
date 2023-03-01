package com.shimmermare.stuffiread.ui.pages.story.info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class StoryInfoPage(val storyId: StoryId) : LoadedPage<Story>() {
    init {
        require(storyId != 0u) { "Can't view non-existing story" }
    }

    @Composable
    override fun Title(app: AppState) {
        val title = remember(status) {
            when (status) {
                Status.LOADING -> "Loading story ID $storyId..."
                Status.LOADED -> "Tag - ${content!!.name} [${content!!.id}]"
                Status.FAILED -> "Failed to load story with ID $storyId"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState): Story {
        return app.storyArchive!!.storyService.getStoryByIdOrThrow(storyId)
    }

    @Composable
    override fun LoadingError( app: AppState) {
        Napier.e(error) { "Failed to load story $storyId" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load story $storyId",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    app.router.goTo(StoryInfoPage(storyId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        StoryInfo(app, content!!)
    }
}