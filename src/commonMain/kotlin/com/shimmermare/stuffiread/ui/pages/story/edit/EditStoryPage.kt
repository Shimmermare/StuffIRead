package com.shimmermare.stuffiread.ui.pages.story.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyCoverService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyFilesService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.story.SavingStoryForm
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class EditStoryPage(
    private val storyId: StoryId,
) : LoadedPage<StoryFormData>() {
    @Composable
    override fun Title() {
        val title = remember(storyId, status) {
            when (status) {
                Status.LOADING -> "Loading story $storyId..."
                Status.LOADED -> "Story (Editing) - ${content!!.story.name} [${storyId}]"
                Status.FAILED -> "Failed to load story $storyId!"
            }

        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(): StoryFormData {
        return withContext(coroutineContext) {
            val story = async { storyService.getStoryByIdOrThrow(storyId) }
            val cover = async { storyCoverService.getStoryCover(storyId) }
            val files = async { storyFilesService.getStoryFiles(storyId) }
            StoryFormData(
                story = story.await(),
                cover = cover.await(),
                files = files.await()
            )
        }
    }

    @Composable
    override fun LoadedContent() {

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            SavingStoryForm(
                prefillData = content!!,
                onSubmittedAndSaved = {
                    Router.goTo(StoryInfoPage(it.story.id))
                },
                onBack = { Router.goTo(StoryInfoPage(storyId)) },
                creationMode = false,
            )
        }
    }
}