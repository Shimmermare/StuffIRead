package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.util.LoadingContainer
import io.github.aakira.napier.Napier

@Composable
fun StoryInfo(app: AppState, story: Story) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.widthIn(min = 1000.dp, max = 1600.dp).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            Box(
                modifier = Modifier.weight(1F)
            ) {
                LeftBlock(app, story)
            }
            Box(
                modifier = Modifier.weight(1F)
            ) {
                RightBlock(app, story)
            }
        }
    }
}

@Composable
private fun LeftBlock(app: AppState, story: Story) {
    val tags = remember {
        app.storyArchive!!.tagService.getExtendedTagsByIds(story.tags)
            .flatMap { tag ->
                buildList {
                    add(TagWithCategory(tag.tag, tag.category))
                    addAll(tag.impliedTags)
                    addAll(tag.indirectlyImpliedTags)
                }
            }
            .distinctBy { it.tag.id }
            .sortedWith(TagWithCategory.DEFAULT_ORDER.thenComparing { a, b ->
                val aExplicit = story.tags.contains(a.tag.id)
                val bExplicit = story.tags.contains(b.tag.id)
                // Explicit first
                bExplicit.compareTo(aExplicit)
            })
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StoryCoverImage(
                storyCoverService = app.storyArchive!!.storyCoverService,
                storyId = story.id,
                modifier = Modifier.height(200.dp).widthIn(min = 100.dp, max = 300.dp),
            )
            SelectionContainer {
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(story.name.toString(), style = MaterialTheme.typography.h5)
                    Text(story.author.toString(), style = MaterialTheme.typography.h6)

                    Column {
                        if (story.published != null) {
                            Date(story.published, label = "Published:", style = MaterialTheme.typography.subtitle1)
                        } else {
                            Text("Unknown publishing date", style = MaterialTheme.typography.subtitle1)
                        }
                        if (story.changed != null && story.published != story.changed) {
                            Date(story.changed, label = "Last changed:", style = MaterialTheme.typography.subtitle1)
                        }
                    }
                }
            }
        }

        if (tags.isNotEmpty()) {
            ChipVerticalGrid {
                tags.forEach { tag -> TagName(app.router, tag, indirect = !story.tags.contains(tag.tag.id)) }
            }
        }

        if (story.url.isPresent) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("URL", style = MaterialTheme.typography.h6)
                val uriHandler = LocalUriHandler.current
                Text(
                    text = story.url.toString(),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.clickable { uriHandler.openUri(story.url.toString()) },
                )
            }
        }

        if (story.description.isPresent) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Description", style = MaterialTheme.typography.h6)
                Text(story.description.toString())
            }
        } else {
            Text("No description", style = MaterialTheme.typography.h6)
        }

        // TODO: Story picker sequels
        Text("TODO: Sequels", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.error)
        // TODO: Story picker prequels
        Text("TODO: Prequels", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.error)

        LoadingContainer(
            key = story.id,
            loader = { app.storyArchive!!.storyFilesService.getStoryFilesMeta(it) }
        ) { files ->
            if (files.isNotEmpty()) {
                Column {
                    val uriHandler = LocalUriHandler.current
                    Text("Story has ${files.size} archived files", style = MaterialTheme.typography.h6)
                    Button(onClick = {
                        val filesDirUri = app.storyArchive!!.storyFilesService.getStoryFilesDirectory(story.id).toUri()
                        Napier.i { "Opening story files dir for ${story.id}: $filesDirUri" }
                        uriHandler.openUri(filesDirUri.toASCIIString())
                    }) {
                        Text("Open files directory")
                    }
                    StoryFileListView(files)
                }
            } else {
                Text("Story has no archived files", style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Composable
private fun RightBlock(
    app: AppState,
    story: Story
) {
    SelectionContainer {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column {
                Date(story.created, label = "First added to archive:", style = MaterialTheme.typography.subtitle1)
                Date(story.updated, label = "Last update in archive:", style = MaterialTheme.typography.subtitle1)
            }

            Column {
                Text("Times read: " + story.timesRead, style = MaterialTheme.typography.subtitle1)
                if (story.firstRead != null) {
                    Date(story.firstRead, label = "First read:", style = MaterialTheme.typography.subtitle1)
                }
                if (story.lastRead != null) {
                    Date(story.lastRead, label = "Last read:", style = MaterialTheme.typography.subtitle1)
                }
            }

            if (story.score != null) {
                Row {
                    Text("Score: ", style = MaterialTheme.typography.subtitle1)
                    StoryScore(app, story.score)
                }
            } else {
                Text("Story not scored")
            }

            if (story.review.isPresent) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Review", style = MaterialTheme.typography.h6)
                    Text(story.review.toString())
                }
            } else {
                Text("No review", style = MaterialTheme.typography.h6)
            }
        }
    }
}