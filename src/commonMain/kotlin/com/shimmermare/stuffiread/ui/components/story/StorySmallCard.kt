package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage

val StorySmallCardDefaultModifier = Modifier
    .padding(start = 2.5.dp, end = 5.dp, top = 2.5.dp, bottom = 5.dp)

val StorySmallCardDefaultContentModifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 10.dp, vertical = 5.dp)

@Composable
fun SmallStoryCard(
    story: Story,
    modifier: Modifier = StorySmallCardDefaultModifier,
    contentModifier: Modifier = StorySmallCardDefaultContentModifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.border(1.dp, Color.LightGray)
                .let { if (onClick != null) it.clickable(onClick = onClick) else it }, elevation = 3.dp
        ) {
            Column(
                modifier = contentModifier
            ) {
                Text(
                    text = story.name.value,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "by " + (story.author.value ?: "unknown author"),
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SmallStoryCardRoutableWithPreview(
    story: Story,
    modifier: Modifier = StorySmallCardDefaultModifier,
    contentModifier: Modifier = StorySmallCardDefaultContentModifier,
) {
    SmallStoryCardWithPreview(
        story = story,
        modifier = modifier,
        contentModifier = contentModifier,
        onClick = { Router.goTo(StoryInfoPage(storyId = story.id)) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallStoryCardWithPreview(
    story: Story,
    modifier: Modifier = StorySmallCardDefaultModifier,
    contentModifier: Modifier = StorySmallCardDefaultContentModifier,
    onClick: (() -> Unit)? = null,
) {
    TooltipArea(
        tooltip = {
            Box(
                modifier = Modifier.width(800.dp)
            ) {
                StoryCard(story)
            }
        }
    ) {
        SmallStoryCard(
            story = story,
            modifier = modifier,
            contentModifier = contentModifier,
            onClick = onClick
        )
    }
}