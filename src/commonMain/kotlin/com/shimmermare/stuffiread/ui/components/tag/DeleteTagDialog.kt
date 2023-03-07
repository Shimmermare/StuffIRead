package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storySearchService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun DeleteTagDialog(
    tag: ExtendedTag,
    onDismissRequest: () -> Unit,
    onDeleted: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    ConfirmationDialog(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Confirm removal of ")
                TagName(tag)
            }
        },
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            coroutineScope.launch {
                deleteTag(tag)
                onDeleted()
            }
        }
    ) {
        LoadingContainer(
            key = tag.tag.id,
            loader = { storySearchService.getStoriesByFilter(StoryFilter(tagsPresent = setOf(tag.tag.id))).count() }
        ) { storiesUsingTagCount ->
            if (storiesUsingTagCount > 0) {
                Text("Tag is used by $storiesUsingTagCount story(s)")
            }
        }
        if (tag.implyingTags.isNotEmpty()) {
            Column {
                Text("Tag is implied by ${tag.implyingTags.size} other tag(s)")
                ChipVerticalGrid {
                    tag.implyingTags.forEach {
                        TagName(it)
                    }
                }
            }
        }
        if (tag.impliedTags.isNotEmpty()) {
            Column {
                Text("Tag implies ${tag.impliedTags.size} other tag(s)")
                ChipVerticalGrid {
                    tag.impliedTags.forEach {
                        TagName(it)
                    }
                }
            }
        }
    }
}

private suspend fun deleteTag(tag: ExtendedTag) {
    // Delete tag from stories
    storySearchService.getStoriesByFilter(StoryFilter(tagsPresent = setOf(tag.tag.id))).onEach { story ->
        val updated = story.copy(tags = story.tags - tag.tag.id)
        storyService.updateStory(updated)
    }.collect()

    if (tag.implyingTags.isNotEmpty()) {
        val toUpdate = tag.implyingTags.map {
            it.tag.copy(impliedTagIds = it.tag.impliedTagIds - tag.tag.id)
        }
        tagService.updateTags(toUpdate)
    }

    tagService.deleteTagById(tag.tag.id)
}