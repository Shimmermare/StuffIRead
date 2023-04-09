package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storySearchService
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.story.SmallStoryCardRoutableWithPreview
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryNameRoutable
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.tags.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.util.remember
import de.comahe.i18n4k.strings.LocalizedStringFactory1
import kotlinx.coroutines.flow.toList

private const val STORIES_WITH_TAG_PREVIEW_COUNT = 5

@Composable
fun TagInfo(tag: ExtendedTag) {

    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { Router.goTo(EditTagPage.createCopy(tag.tag)) }) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(onClick = { Router.goTo(EditTagPage.edit(tag.tag)) }) {
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
                modifier = Modifier.widthIn(min = 800.dp, max = 1200.dp)
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
                Router.goTo(TagsPage())
            },
            onDismissRequest = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PropertiesBlock(tag: ExtendedTag) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagInfo_name.remember(), style = MaterialTheme.typography.h6)
                TagNameRoutable(tag)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagInfo_category.remember(), style = MaterialTheme.typography.h6)
                TagCategoryNameRoutable(tag.category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagInfo_description.remember(), style = MaterialTheme.typography.h6)
                if (tag.tag.description.isPresent) {
                    Text(tag.tag.description.value!!)
                } else {
                    Text(
                        Strings.components_tagInfo_description_noDescription.remember(),
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray
                    )
                }
            }

            RelatedTagsBlock(tag.implyingTags, Strings.components_tagInfo_implyingTags_title)
            RelatedTagsBlock(tag.indirectlyImplyingTags, Strings.components_tagInfo_indirectlyImplyingTags_title)
            RelatedTagsBlock(tag.impliedTags, Strings.components_tagInfo_impliedTags_title)
            RelatedTagsBlock(tag.indirectlyImpliedTags, Strings.components_tagInfo_indirectlyImpliedTags_title)

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagInfo_created.remember(), style = MaterialTheme.typography.h6)
                Date(tag.tag.created)
            }
            if (tag.tag.created != tag.tag.updated) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(Strings.components_tagInfo_updated.remember(), style = MaterialTheme.typography.h6)
                    Date(tag.tag.updated)
                }
            }
        }
    }
}

@Composable
private fun RelatedTagsBlock(tags: List<TagWithCategory>, title: LocalizedStringFactory1) {
    if (tags.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = title.remember(tags.size),
                style = MaterialTheme.typography.h6
            )
            ChipVerticalGrid {
                tags.forEach {
                    TagNameRoutable(it)
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(
    tagId: TagId
) {
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
            Text(Strings.components_tagInfo_storiesWithTag_title.remember(storiesWithTag.size), fontWeight = FontWeight.Bold)
            if (storiesWithTag.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    storiesWithTag.take(STORIES_WITH_TAG_PREVIEW_COUNT).forEach {
                        SmallStoryCardRoutableWithPreview(it)
                    }
                    if (storiesWithTag.size > STORIES_WITH_TAG_PREVIEW_COUNT) {
                        Text(Strings.components_tagInfo_storiesWithTag_andMore.remember(storiesWithTag.size - STORIES_WITH_TAG_PREVIEW_COUNT))
                    }
                }
                Button(
                    onClick = {
                        val filter = StoryFilter(tagsPresent = setOf(tagId))
                        Router.goTo(StoriesPage(presetFilter = filter))
                    }
                ) {
                    Text(Strings.components_tagInfo_storiesWithTag_showAllButton.remember())
                }
            }
        }
    }
}

