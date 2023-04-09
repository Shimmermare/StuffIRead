package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.story.DeleteStoryDialog
import com.shimmermare.stuffiread.ui.components.story.StoryInfo
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class StoryInfoPage(val storyId: StoryId) : LoadedPage<Story>() {
    init {
        require(storyId != StoryId.None) { "Can't view non-existing story" }
    }

    @Composable
    override fun Title() {
        val title = remember(status) {
            when (status) {
                Status.LOADING -> Strings.page_storyInfo_loading() + " [" + storyId + "]"
                Status.LOADED -> Strings.page_storyInfo_title(content!!.name) + " [" + storyId + "]"
                Status.FAILED -> Strings.page_storyInfo_error_failedToLoad(storyId)
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(): Story {
        return storyService.getStoryByIdOrThrow(storyId)
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load story $storyId" }

        Router.goTo(
            ErrorPage(
                title = Strings.page_storyInfo_error_failedToLoad(storyId),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_storyInfo_error_failedToLoad_tryAgainButton()) {
                    Router.goTo(StoryInfoPage(storyId))
                })
            )
        )
    }

    @Composable
    override fun LoadedContent() {
        var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    FloatingActionButton(onClick = { Router.goTo(EditStoryPage(content!!.id)) }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    FloatingActionButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            }
        ) {
            VerticalScrollColumn {
                StoryInfo(content!!, onRefreshInfoRequest = { Router.goTo(StoryInfoPage(storyId)) })
            }
        }

        if (showDeleteDialog) {
            DeleteStoryDialog(
                story = content!!,
                onDismissRequest = { showDeleteDialog = false },
                onDeleted = {
                    showDeleteDialog = false
                    Router.goTo(StoriesPage())
                }
            )
        }
    }
}