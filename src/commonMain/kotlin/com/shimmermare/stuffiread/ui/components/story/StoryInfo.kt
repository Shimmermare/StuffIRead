package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.stories.StoryRead
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyFilesService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storySearchService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.date.DateWithLabel
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.ExtendedTooltipArea
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.components.text.TextURI
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun StoryInfo(story: Story, onRefreshInfoRequest: () -> Unit) {
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
                RightBlock(story, onRefreshInfoRequest)
            }
        }
    }
}

@Composable
private fun LeftBlock(story: Story) {
    val tags = remember {
        tagService.getTagsWithCategoryByIdsIncludingImplied(story.tags).sortedWith(
            TagWithCategory.DEFAULT_ORDER.thenComparing { a, b ->
                val aExplicit = story.tags.contains(a.tag.id)
                val bExplicit = story.tags.contains(b.tag.id)
                // Explicit first
                bExplicit.compareTo(aExplicit)
            }
        )
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

                    Text(
                        text = if (story.author.isPresent) {
                            story.author.toString()
                        } else {
                            Strings.components_storyInfo_author_unknown.remember()
                        },
                        style = MaterialTheme.typography.h6,
                    )


                    Column {
                        if (story.published != null) {
                            DateWithLabel(Strings.components_storyInfo_published.remember(), story.published)
                        } else {
                            Text(Strings.components_storyInfo_published_unknown.remember())
                        }
                        if (story.changed != null && story.published != story.changed) {
                            DateWithLabel(Strings.components_storyInfo_changed.remember(), story.changed)
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
                Text(Strings.components_storyInfo_url.remember(), style = MaterialTheme.typography.h6)
                TextURI(story.url.toString(), style = MaterialTheme.typography.subtitle1)
            }
        }

        if (story.description.isPresent) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_storyInfo_description.remember(), style = MaterialTheme.typography.h6)
                Text(story.description.toString())
            }
        } else {
            Text(Strings.components_storyInfo_description_noDescription.remember(), style = MaterialTheme.typography.h6)
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
            Text(Strings.components_storyInfo_sequels.remember(), style = MaterialTheme.typography.h6)
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
    LoadingContainer(
        key = id,
        loader = { storySearchService.getStoriesByFilter(StoryFilter(isPrequelOf = setOf(id))).toList() }
    ) { stories ->
        if (stories.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(Strings.components_storyInfo_prequels.remember(), style = MaterialTheme.typography.h6)
                stories.forEach { SmallStoryCardRoutableWithPreview(story = it) }
            }
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
                Text(components_storyInfo_files_count.remember(files.size), style = MaterialTheme.typography.h6)
                Button(
                    onClick = {
                        val filesDirUri = storyFilesService.getStoryFilesDirectory(story.id).toUri()
                        Napier.i { "Opening story files dir for ${story.id}: $filesDirUri" }
                        uriHandler.openUri(filesDirUri.toASCIIString())
                    }
                ) {
                    Text(Strings.components_storyInfo_files_openDirButton.remember())
                }
                StoryFileListView(files)
            }
        } else {
            Text(Strings.components_storyInfo_files_noFiles.remember(), style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun RightBlock(story: Story, onRefreshInfoRequest: () -> Unit) {
    SelectionContainer {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column {
                DateWithLabel(Strings.components_storyInfo_created.remember(), story.created)
                DateWithLabel(Strings.components_storyInfo_updated.remember(), story.updated)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    components_storyInfo_reads_count.remember(story.reads.size),
                    style = MaterialTheme.typography.subtitle1
                )
                story.reads.sorted().forEach { read ->
                    Date(read.date)
                }
                IReadItJustNowButton(story, onRefreshInfoRequest)
            }

            if (story.score != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(Strings.components_storyInfo_score.remember(), style = MaterialTheme.typography.subtitle1)
                    StoryScore(story.score)
                }
            } else {
                Text(Strings.components_storyInfo_score_missing.remember())
            }

            if (story.review.isPresent) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(Strings.components_storyInfo_review.remember(), style = MaterialTheme.typography.h6)
                    Text(story.review.toString())
                }
            } else {
                Text(Strings.components_storyInfo_review_missing.remember(), style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Composable
private fun IReadItJustNowButton(story: Story, onRefreshInfoRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    ExtendedTooltipArea(
        tooltip = { Text(Strings.components_storyInfo_reads_addNew_tooltip.remember()) }
    ) {
        DisableSelection {
            Button(
                onClick = {
                    coroutineScope.launch {
                        storyService.updateStory(story.copy(reads = story.reads + StoryRead(Clock.System.now())))
                        onRefreshInfoRequest()
                    }
                }
            ) {
                Text(Strings.components_storyInfo_reads_addNew_button.remember())
            }
        }
    }
}

private val components_storyInfo_files_count = PluralLocalizedString(
    Strings.components_storyInfo_files_count_other,
    Strings.components_storyInfo_files_count_one,
    Strings.components_storyInfo_files_count_two,
    Strings.components_storyInfo_files_count_few,
    Strings.components_storyInfo_files_count_many,
    Strings.components_storyInfo_files_count_other,
)
private val components_storyInfo_reads_count = PluralLocalizedString(
    Strings.components_storyInfo_reads_count_other,
    Strings.components_storyInfo_reads_count_one,
    Strings.components_storyInfo_reads_count_two,
    Strings.components_storyInfo_reads_count_few,
    Strings.components_storyInfo_reads_count_many,
    Strings.components_storyInfo_reads_count_other,
)