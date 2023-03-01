package com.shimmermare.stuffiread.ui.pages.story.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.components.story.StoryForm
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.pages.story.create.importing.StoryImportForm
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import com.shimmermare.stuffiread.ui.routing.Page
import kotlinx.coroutines.launch

class CreateStoryPage : Page {
    @Composable
    override fun Title(app: AppState) {
        Text(text = "New story", maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(app: AppState) {
        val coroutineScope = rememberCoroutineScope()
        var formData: StoryFormData? by remember { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            AnimatedFadeIn {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (formData == null) {
                        ImportOrCreateNew(onSelected = { formData = it })
                    } else {
                        StoryForm(
                            app = app,
                            prefillData = formData!!,
                            onSubmit = {
                                coroutineScope.launch {
                                    val created = app.storyArchive!!.storyService.createStory(it.story)
                                    app.storyArchive!!.storyFilesService.updateStoryFiles(created.id, it.files)
                                    app.router.goTo(StoryInfoPage(created.id))
                                }
                            },
                            onBack = { formData = null },
                            submitButtonText = "Create",
                            canSubmitWithoutChanges = true
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ImportOrCreateNew(onSelected: (StoryFormData) -> Unit) {
        // Hide manual import suggestion and move content to top if user wants to import
        var importFormOpen: Boolean by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp,
                alignment = if (importFormOpen) Alignment.Top else Alignment.CenterVertically
            ),
        ) {
            if (!importFormOpen) {
                Button(onClick = { onSelected(StoryFormData(Story(name = StoryName("Story name")))) }) {
                    Text("Add manually")
                }
                Text("OR", style = MaterialTheme.typography.h5)
            }
            StoryImportForm(
                onOpenStateChange = { importFormOpen = it },
                { onSelected(StoryFormData(it.story, it.files)) })
        }
    }
}