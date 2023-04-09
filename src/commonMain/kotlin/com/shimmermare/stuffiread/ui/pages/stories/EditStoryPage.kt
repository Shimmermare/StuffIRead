package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.CurrentLocale
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyCoverService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyFilesService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.story.SavingStoryForm
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class EditStoryPage(
    private val storyId: StoryId,
) : LoadedPage<StoryFormData>() {
    @Composable
    override fun Title() {
        val title = remember(storyId, status, CurrentLocale) {
            when (status) {
                Status.LOADING -> Strings.page_storyEdit_loading()
                Status.LOADED -> Strings.page_storyEdit_title(content!!.story.name) + "[" + storyId + "]"
                Status.FAILED -> Strings.page_storyEdit_error_failedToLoad(storyId)
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    override suspend fun load(): StoryFormData {
        return withContext(coroutineContext) {
            val story = async { storyService.getStoryByIdOrThrow(storyId) }
            val prequels = async { storyService.getStoryPrequelIds(storyId).toSet() }
            val cover = async { storyCoverService.getStoryCover(storyId) }
            val files = async { storyFilesService.getStoryFiles(storyId) }
            StoryFormData(
                story = story.await(),
                originalPrequels = prequels.await(),
                cover = cover.await(),
                files = files.await()
            )
        }
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load story $storyId" }

        Router.goTo(
            ErrorPage(
                title = Strings.page_storyEdit_error_failedToLoad(storyId),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_storyEdit_error_failedToLoad_tryAgainButton()) {
                    Router.goTo(EditStoryPage(storyId))
                })
            )
        )
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