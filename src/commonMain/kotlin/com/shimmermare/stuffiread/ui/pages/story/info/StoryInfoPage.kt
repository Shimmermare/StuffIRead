package com.shimmermare.stuffiread.ui.pages.story.info

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
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.story.DeleteStoryDialog
import com.shimmermare.stuffiread.ui.components.story.StoryInfo
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.story.edit.EditStoryPage
import io.github.aakira.napier.Napier

class StoryInfoPage(val storyId: StoryId) : LoadedPage<Story>() {
    init {
        require(storyId != StoryId.None) { "Can't view non-existing story" }
    }

    @Composable
    override fun Title(app: AppState) {
        val title = remember(status) {
            when (status) {
                Status.LOADING -> "Loading story ID $storyId..."
                Status.LOADED -> "Story - ${content!!.name} [${content!!.id}]"
                Status.FAILED -> "Failed to load story with ID $storyId"
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState): Story {
        return app.storyArchive!!.storyService.getStoryByIdOrThrow(storyId)
    }

    @Composable
    override fun LoadingError(app: AppState) {
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
        var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    FloatingActionButton(onClick = { app.router.goTo(EditStoryPage(content!!.id)) }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    FloatingActionButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            }
        ) {
            VerticalScrollColumn {
                StoryInfo(content!!)
            }
        }

        if (showDeleteDialog) {
            DeleteStoryDialog(
                app.storyArchive!!.storyService,
                story = content!!,
                onDelete = {
                    showDeleteDialog = false
                    app.router.goTo(StoriesPage())
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}