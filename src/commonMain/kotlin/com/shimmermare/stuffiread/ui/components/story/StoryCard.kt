package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.stories.cover.StoryCoverService
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.pages.story.info.StoryInfoPage
import com.shimmermare.stuffiread.ui.util.OptionalLoadingContainer
import io.github.aakira.napier.Napier

@Composable
fun StoryCard(
    app: AppState,
    story: Story,
    visible: Boolean
) {
    Box(
        modifier = Modifier
            .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
            .let { if (visible) it else it.background(Color.LightGray) }
            .height(300.dp)
    ) {
        if (visible) {
            VisibleStoryCard(app, story)
        }
    }
}

@Composable
private fun VisibleStoryCard(
    app: AppState,
    story: Story,
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
                    Cover(
                        storyCoverService = app.storyArchive!!.storyCoverService,
                        storyId = story.id
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
                if (story.description.isPresent) {
                    Text(story.description.toString())
                }
            }
        }
    }
}

@Composable
private fun Cover(
    storyCoverService: StoryCoverService,
    storyId: StoryId
) {
    Box(
        modifier = Modifier.height(150.dp).widthIn(min = 100.dp, max = 250.dp),
        contentAlignment = Alignment.Center
    ) {
        OptionalLoadingContainer(
            key = storyId,
            loader = {
                storyCoverService.getStoryCover(storyId)?.let {
                    BitmapPainter(loadImageBitmap(it.data.inputStream()))
                }
            },
            onError = {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load cover", color = MaterialTheme.colors.error)
                }
                Napier.e(it) { "Failed to load cover for $storyId" }
            }
        ) { cover ->
            if (cover != null) {
                Image(
                    painter = cover,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}