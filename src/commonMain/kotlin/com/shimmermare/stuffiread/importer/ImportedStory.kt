package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.cover.StoryCover
import com.shimmermare.stuffiread.stories.file.StoryFile
import kotlinx.datetime.Instant

data class ImportedStory(
    val author: StoryAuthor = StoryAuthor.UNKNOWN,
    val name: StoryName,
    val url: StoryURL = StoryURL.NONE,
    val description: StoryDescription = StoryDescription.NONE,
    val published: Instant? = null,
    val changed: Instant? = published,
    val tags: Set<String> = emptySet(),
    val cover: StoryCover? = null,
    val files: List<StoryFile> = emptyList()
)
