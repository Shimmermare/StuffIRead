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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage

@Composable
fun StoryCard(
    app: AppState,
    story: Story,
) {
    Box(
        modifier = Modifier
            .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
            .height(300.dp)
    ) {
        Surface(
            modifier = Modifier
                .border(1.dp, Color.LightGray)
                .clickable { app.router.goTo(StoryInfoPage(storyId = story.id)) },
            elevation = 6.dp
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
                                text = story.name,
                                style = MaterialTheme.typography.h6,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = story.author ?: "Unknown author",
                                style = MaterialTheme.typography.subtitle1,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(2))
                                .width(100.dp)
                                .height(120.dp)
                        )
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Score: ",
                                    style = MaterialTheme.typography.subtitle1,
                                    maxLines = 1
                                )
                                StoryScore(app, story.score)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (story.description != null) {
                        Text(story.description)
                    }
                }
            }
        }
    }
}

data class StoryCardData(
    val story: Story,
    val previewImage: Nothing,
    val previewTags: List<PreviewTag>,
)

data class PreviewTag(
    val tag: Tag,
    val category: TagCategory,
    val explicit: Boolean
)