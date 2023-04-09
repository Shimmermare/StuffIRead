package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.util.remember
import kotlinx.coroutines.launch

@Composable
fun DeleteStoryDialog(story: Story, onDismissRequest: () -> Unit, onDeleted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    ConfirmationDialog(
        title = {
            Text(Strings.components_deleteStoryDialog_title.remember(story.name))
        },
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            coroutineScope.launch {
                storyService.deleteStoryById(story.id)
                onDeleted()
            }
        }
    ) {
        Text(Strings.components_deleteStoryDialog_description.remember())
    }
}