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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import com.shimmermare.stuffiread.ui.router
import com.shimmermare.stuffiread.ui.tagService

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
    val router = router
    val tagService = tagService

    val tags = remember(story.id) {
        tagService.getTagsWithCategoryByIds(story.tags).sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    Surface(
        modifier = Modifier
            .border(1.dp, Color.LightGray)
            .clickable { router.goTo(StoryInfoPage(storyId = story.id)) },
        elevation = 3.dp
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
                            text = story.author.value ?: "Unknown author",
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
                        Text(
                            text = "Published: " + story.published,
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 1
                        )
                    }
                    if (story.changed != null) {
                        Text(
                            text = "Last changed: " + story.changed,
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 1
                        )
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
                if (tags.isNotEmpty()) {
                    ChipVerticalGrid {
                        tags.take(PREVIEW_TAG_COUNT).forEach {
                            TagNameRoutable(it)
                        }
                        if (tags.size > PREVIEW_TAG_COUNT) {
                            Box(
                                modifier = Modifier.height(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("and ${tags.size - PREVIEW_TAG_COUNT} more")
                            }
                        }
                    }
                }
                if (story.description.isPresent) {
                    Column(
                        modifier = Modifier.weight(1F, false)
                    ) {
                        Text("Description", fontWeight = FontWeight.Bold)
                        Text(story.description.toString(), overflow = TextOverflow.Ellipsis)
                    }
                }
                if (story.review.isPresent) {
                    Column(
                        modifier = Modifier.weight(1F, false)
                    ) {
                        Text("Review", fontWeight = FontWeight.Bold)
                        Text(story.review.toString(), overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

