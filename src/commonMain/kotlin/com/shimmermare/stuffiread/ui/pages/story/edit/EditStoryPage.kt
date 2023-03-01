package com.shimmermare.stuffiread.ui.pages.story.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.story.StoryForm
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class EditStoryPage(
    private val storyId: StoryId,
) : LoadedPage<StoryFormData>() {
    @Composable
    override fun Title(app: AppState) {
        val title = remember(storyId, status) {
            when (status) {
                Status.LOADING -> "Loading story $storyId..."
                Status.LOADED -> "Story (Editing) - ${content!!.story.name} [${storyId}]"
                Status.FAILED -> "Failed to load story $storyId!"
            }

        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(app: AppState): StoryFormData {
        return withContext(coroutineContext) {
            val story = async { app.storyArchive!!.storyService.getStoryByIdOrThrow(storyId) }
            val files = async { app.storyArchive!!.storyFilesService.getStoryFiles(storyId) }
            StoryFormData(
                story = story.await(),
                files = files.await()
            )
        }
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            StoryForm(
                app = app,
                prefillData = content!!,
                onSubmit = {
                    coroutineScope.launch {
                        val updated = app.storyArchive!!.storyService.updateStory(it.story)
                        app.storyArchive!!.storyFilesService.updateStoryFiles(updated.id, it.files)
                        app.router.goTo(StoryInfoPage(updated.id))
                    }
                },
                onBack = { app.router.goTo(StoryInfoPage(storyId)) },
                submitButtonText = "Save",
                canSubmitWithoutChanges = false
            )
        }
    }
}