package com.shimmermare.stuffiread.ui.components.story.importing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.input.ExtendedOutlinedTextField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.story.StoryFormData
import com.shimmermare.stuffiread.ui.components.tag.TagPicker

/**
 * Map imported data to valid app story.
 */
@Composable
fun ImportTagMappingForm(
    importedStory: ImportedStory,
    onMapped: (StoryFormData) -> Unit
) {
    var formData: StoryFormData by remember {
        mutableStateOf(
            StoryFormData(
                story = Story(
                    author = importedStory.author,
                    name = importedStory.name,
                    url = importedStory.url,
                    description = importedStory.description,
                    published = importedStory.published,
                    changed = importedStory.changed,
                    tags = emptySet(),
                ),
                cover = importedStory.cover,
                files = importedStory.files,
            )
        )
    }

    // Try auto-match by name
    var mappedTags: Map<String, TagId> by remember {
        if (importedStory.tags.isEmpty()) return@remember mutableStateOf(emptyMap())
        val tagsToMapLowered = importedStory.tags.associateBy { it.lowercase() }
        mutableStateOf(
            tagService.getTags()
                .filter { tagsToMapLowered.contains(it.name.value.lowercase()) }
                .associate { tagsToMapLowered[it.name.value.lowercase()]!! to it.id }
        )
    }

    LaunchedEffect(importedStory) {
        if (importedStory.tags.isEmpty()) {
            onMapped(formData)
        }
    }

    if (importedStory.tags.isNotEmpty()) {
        VerticalScrollColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        ) {
            Text("Map imported tags")
            importedStory.tags.forEach { tagName ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.width(800.dp).height(30.dp)
                ) {
                    ExtendedOutlinedTextField(
                        value = tagName,
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier.weight(1F),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Icon(Icons.Filled.ArrowRightAlt, null)
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1F)
                    ) {
                        TagPicker(
                            title = "Pick mapped tack",
                            pickedTagId = mappedTags.getOrDefault(tagName, TagId.None),
                            onPick = { mappedTags = mappedTags + (tagName to it) }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    formData = formData.copy(story = formData.story.copy(tags = mappedTags.values.toSet()))
                    onMapped(formData)
                }
            ) {
                Text("Continue")
            }
        }
    }
}