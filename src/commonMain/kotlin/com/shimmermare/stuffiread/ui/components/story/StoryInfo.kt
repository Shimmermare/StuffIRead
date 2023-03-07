package com.shimmermare.stuffiread.ui.components.story

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
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyFilesService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storySearchService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.components.text.TextURI
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.toList

@Composable
fun StoryInfo(story: Story) {
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
                LeftBlock(story)
            }
            Box(
                modifier = Modifier.weight(1F)
            ) {
                RightBlock(story)
            }
        }
    }
}

@Composable
private fun LeftBlock(story: Story) {
    val tags = remember {
        tagService.getExtendedTagsByIds(story.tags).flatMap { tag ->
            buildList {
                add(TagWithCategory(tag.tag, tag.category))
                addAll(tag.impliedTags)
                addAll(tag.indirectlyImpliedTags)
            }
        }.distinctBy { it.tag.id }.sortedWith(TagWithCategory.DEFAULT_ORDER.thenComparing { a, b ->
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
                storyId = story.id,
                modifier = Modifier.height(200.dp).widthIn(min = 100.dp, max = 300.dp),
            )
            SelectionContainer {
                Column(
                    modifier = Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(10.dp)
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
                tags.forEach { tag -> TagNameRoutable(tag, indirect = !story.tags.contains(tag.tag.id)) }
            }
        }

        if (story.url.isPresent) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("URL", style = MaterialTheme.typography.h6)
                TextURI(story.url.toString(), style = MaterialTheme.typography.subtitle1)
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

        StorySequels(story.sequels)
        StoryPrequels(story.id)
        StoryFilesInfo(story)
    }
}

@Composable
private fun StorySequels(ids: Set<StoryId>) {
    if (ids.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Sequels", style = MaterialTheme.typography.h6)
            LoadingContainer(
                key = ids,
                loader = { ids -> storyService.getStoriesByIds(ids).toList().sortedBy { it.name } },
            ) { stories ->
                stories.forEach { SmallStoryCardRoutableWithPreview(story = it) }
            }
        }
    }
}

@Composable
private fun StoryPrequels(id: StoryId) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("Prequels", style = MaterialTheme.typography.h6)
        LoadingContainer(
            key = id,
            loader = { storySearchService.getStoriesByFilter(StoryFilter(isPrequelOf = setOf(id))).toList() }
        ) { stories ->
            stories.forEach { SmallStoryCardRoutableWithPreview(story = it) }
        }
    }
}

@Composable
private fun StoryFilesInfo(story: Story) {
    LoadingContainer(
        key = story.id,
        loader = { storyFilesService.getStoryFilesMeta(it) }
    ) { files ->
        if (files.isNotEmpty()) {
            Column {
                val uriHandler = LocalUriHandler.current
                Text("Story has ${files.size} archived files", style = MaterialTheme.typography.h6)
                Button(
                    onClick = {
                        val filesDirUri = storyFilesService.getStoryFilesDirectory(story.id).toUri()
                        Napier.i { "Opening story files dir for ${story.id}: $filesDirUri" }
                        uriHandler.openUri(filesDirUri.toASCIIString())
                    }
                ) {
                    Text("Open files directory")
                }
                StoryFileListView(files)
            }
        } else {
            Text("Story has no archived files", style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun RightBlock(story: Story) {
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
                    StoryScore(story.score)
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