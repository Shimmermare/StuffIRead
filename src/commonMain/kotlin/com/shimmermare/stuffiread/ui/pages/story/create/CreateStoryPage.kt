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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.components.story.SavingStoryForm
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.components.story.importing.StoryImportForm
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import com.shimmermare.stuffiread.ui.router
import com.shimmermare.stuffiread.ui.routing.Page

class CreateStoryPage : Page {
    @Composable
    override fun Title(app: AppState) {
        Text(text = "New story", maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(app: AppState) {
        val router = router

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
                        SavingStoryForm(
                            prefillData = formData!!,
                            onSubmittedAndSaved = {
                                router.goTo(StoryInfoPage(it.story.id))
                            },
                            onBack = { formData = null },
                            creationMode = true
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ImportOrCreateNew(onSelected: (StoryFormData) -> Unit) {
        // Hide manual import suggestion and move content to top if user selected import
        var showManualCreate: Boolean by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp,
                alignment = if (!showManualCreate) Alignment.Top else Alignment.CenterVertically
            ),
        ) {
            if (showManualCreate) {
                Button(onClick = { onSelected(StoryFormData(Story(name = StoryName("Story name")))) }) {
                    Text("Add manually")
                }
                Text("OR", style = MaterialTheme.typography.h5)
            }
            StoryImportForm(
                onSourceSelected = { showManualCreate = (it == null) },
                onImported = onSelected
            )
        }
    }
}