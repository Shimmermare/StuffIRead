package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryService
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.story.edit.EditStoryPage
import com.shimmermare.stuffiread.ui.routing.Router
import kotlinx.coroutines.launch

@Composable
fun StoryInfo(app: AppState, story: Story) {
    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { app.router.goTo(EditStoryPage(story.id)) }) {
                    Icon(Icons.Filled.Edit, null)
                }
                FloatingActionButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, null)
                }
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
        ) {
            Row(
                modifier = Modifier.width(1000.dp)
            ) {
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    PropertiesBlock(app.router, story)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock()
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteStoryDialog(
            app.storyArchive!!.storyService,
            story,
            onDelete = {
                showDeleteDialog = false
                app.router.goTo(StoriesPage())
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PropertiesBlock(router: Router, story: Story) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Name", style = MaterialTheme.typography.h6)
                Text(story.name.value)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Author", style = MaterialTheme.typography.h6)
                if (story.author.isPresent) {
                    Text(story.author.toString())
                } else {
                    Text("Unknown author", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Description", style = MaterialTheme.typography.h6)
                if (story.description.isPresent) {
                    Text(story.description.toString())
                } else {
                    Text("No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Created", style = MaterialTheme.typography.h6)
                Date(story.created)
            }
            if (story.created != story.updated) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Updated", style = MaterialTheme.typography.h6)
                    Date(story.updated)
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "STATS TODO", style = MaterialTheme.typography.h6)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DeleteStoryDialog(storyService: StoryService, story: Story, onDelete: () -> Unit, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    FixedAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Confirm removal of ${story.name}")
            }
        },
        text = {
            Text("All story files and cover image will be deleted.")
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        storyService.deleteStoryById(story.id)
                        onDelete()
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
