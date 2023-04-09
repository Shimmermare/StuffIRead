package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.date.DateWithLabel
import com.shimmermare.stuffiread.ui.components.dialog.InfoDialog
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.DefaultTagNameHeight
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.pages.stories.StoryInfoPage
import com.shimmermare.stuffiread.ui.util.remember

private const val PREVIEW_TAG_COUNT = 20

@Composable
fun StoryCard(
    story: Story,
    visible: Boolean = true,
) {
    Box(
        modifier = Modifier
            .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
            .let { if (visible) it else it.background(Color.LightGray) }
            .height(300.dp)
    ) {
        if (visible) {
            VisibleStoryCard(story)
        }
    }
}

@Composable
private fun VisibleStoryCard(
    story: Story,
) {
    Surface(
        modifier = Modifier
            .border(1.dp, Color.LightGray)
            .clickable { Router.goTo(StoryInfoPage(storyId = story.id)) },
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.fillMaxHeight().weight(1F).padding(end = 10.dp)) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column {
                        Text(
                            text = story.name.value,
                            style = MaterialTheme.typography.h6,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (story.author.isPresent) {
                                story.author.toString()
                            } else {
                                Strings.components_storyInfo_author_unknown.remember()
                            },
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    StoryCoverImage(storyId = story.id)
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                ) {
                    if (story.published != null) {
                        DateWithLabel(Strings.components_storyInfo_published.remember(), story.published)
                    }
                    if (story.changed != null) {
                        DateWithLabel(Strings.components_storyInfo_changed.remember(), story.changed)
                    }
                    if (story.score != null) {
                        StoryScore(story.score)
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StoryCardTags(story)
                if (story.description.isPresent) {
                    Column(
                        modifier = Modifier.weight(1F, false)
                    ) {
                        Text(Strings.components_storyInfo_description.remember(), fontWeight = FontWeight.Bold)
                        Text(story.description.toString(), overflow = TextOverflow.Ellipsis)
                    }
                }
                if (story.review.isPresent) {
                    Column(
                        modifier = Modifier.weight(1F, false)
                    ) {
                        Text(Strings.components_storyInfo_review.remember(), fontWeight = FontWeight.Bold)
                        Text(story.review.toString(), overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryCardTags(story: Story) {
    val tags = remember(story.id) {
        if (story.tags.isEmpty()) {
            emptyList()
        } else {
            tagService.getTagsWithCategoryByIdsIncludingImplied(story.tags).sortedWith(
                TagWithCategory.DEFAULT_ORDER.thenComparing { a, b ->
                    val aExplicit = story.tags.contains(a.tag.id)
                    val bExplicit = story.tags.contains(b.tag.id)
                    // Explicit first
                    bExplicit.compareTo(aExplicit)
                }
            )
        }
    }
    val preview = remember(story.id) {
        // Only explicit tags in preview
        tags.asSequence().filter { story.tags.contains(it.tag.id) }.take(PREVIEW_TAG_COUNT).toList()
    }

    var showFullList: Boolean by remember(story.id) { mutableStateOf(false) }

    if (tags.isNotEmpty()) {
        ChipVerticalGrid {
            preview.forEach {
                TagNameRoutable(it)
            }
            if (tags.size > preview.size) {
                TextButton(
                    onClick = { showFullList = true },
                    modifier = Modifier.height(DefaultTagNameHeight)
                ) {
                    Text(Strings.components_storyCard_tags_andMore.remember(tags.size - preview.size))
                }
            }
        }
    }

    if (showFullList) {
        InfoDialog(
            title = { Text(Strings.components_storyCard_tags_allTagsTitle.remember()) },
            onDismissRequest = { showFullList = false },
        ) {
            ChipVerticalGrid {
                tags.forEach { TagNameRoutable(it, indirect = !story.tags.contains(it.tag.id)) }
            }
        }
    }
}
