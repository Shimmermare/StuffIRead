package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import kotlinx.coroutines.launch

@Composable
fun DeleteStoryDialog(story: Story, onDismissRequest: () -> Unit, onDeleted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    ConfirmationDialog(
        title = {
            Text("Confirm removal of ${story.name}")
        },
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            coroutineScope.launch {
                storyService.deleteStoryById(story.id)
                onDeleted()
            }
        }
    ) {
        Text("All story files and cover image will be deleted.")
    }
}