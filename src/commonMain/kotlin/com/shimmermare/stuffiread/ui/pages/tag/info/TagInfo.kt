package com.shimmermare.stuffiread.ui.pages.tag.info

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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.story.SmallStoryCardRoutableWithPreview
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryNameRoutable
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.router
import com.shimmermare.stuffiread.ui.storySearchService
import kotlinx.coroutines.flow.toList

private const val STORIES_WITH_TAG_PREVIEW_COUNT = 5

@Composable
fun TagInfo(tag: ExtendedTag) {
    val router = router

    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { router.goTo(EditTagPage.createCopy(tag.tag)) }) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(onClick = { router.goTo(EditTagPage.edit(tag.tag)) }) {
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
                modifier = Modifier.width(800.dp)
            ) {
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    PropertiesBlock(tag)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock(tag.tag.id)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTagDialog(
            tag = tag,
            onDeleted = {
                showDeleteDialog = false
                router.goTo(TagsPage())
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PropertiesBlock(tag: ExtendedTag) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Name", style = MaterialTheme.typography.h6)
                TagNameRoutable(tag)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Category", style = MaterialTheme.typography.h6)
                TagCategoryNameRoutable(tag.category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.h6)
                if (tag.tag.description.isPresent) {
                    Text(text = tag.tag.description.value!!)
                } else {
                    Text(text = "No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }
            if (tag.implyingTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Implied by ${tag.implyingTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.implyingTags.forEach {
                            TagNameRoutable(it)
                        }
                    }
                }
            }
            if (tag.indirectlyImplyingTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Indirectly implied by ${tag.indirectlyImplyingTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.indirectlyImplyingTags.forEach {
                            TagNameRoutable(it, indirect = true)
                        }
                    }
                }
            }
            if (tag.impliedTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Implies ${tag.impliedTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.impliedTags.forEach {
                            TagNameRoutable(it)
                        }
                    }
                }
            }
            if (tag.indirectlyImpliedTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Indirectly implies ${tag.indirectlyImpliedTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.indirectlyImpliedTags.forEach {
                            TagNameRoutable(it, indirect = true)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Created", style = MaterialTheme.typography.h6)
                Date(tag.tag.created)
            }
            if (tag.tag.created != tag.tag.updated) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = "Updated", style = MaterialTheme.typography.h6)
                    Date(tag.tag.updated)
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(
    tagId: TagId
) {
    val router = router
    val storySearchService = storySearchService
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LoadingContainer(
            key = tagId,
            loader = {
                storySearchService.getStoriesByFilter(StoryFilter(tagsPresent = setOf(tagId)))
                    .toList().sortedBy { it.name }
            }
        ) { storiesWithTag ->
            if (storiesWithTag.isEmpty()) {
                Text("No stories with tag", fontWeight = FontWeight.Bold)
            } else {
                Text("${storiesWithTag.size} story(s) with tag", fontWeight = FontWeight.Bold)
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    storiesWithTag.take(STORIES_WITH_TAG_PREVIEW_COUNT).forEach {
                        SmallStoryCardRoutableWithPreview(it)
                    }
                    if (storiesWithTag.size > STORIES_WITH_TAG_PREVIEW_COUNT) {
                        Text("and ${storiesWithTag.size - STORIES_WITH_TAG_PREVIEW_COUNT} more")
                    }
                }
                Button(
                    onClick = {
                        val filter = StoryFilter(tagsPresent = setOf(tagId))
                        router.goTo(StoriesPage(presetFilter = filter))
                    }
                ) {
                    Text("Show all")
                }
            }
        }
    }
}

