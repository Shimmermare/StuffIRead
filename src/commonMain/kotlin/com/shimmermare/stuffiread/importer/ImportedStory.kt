package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.cover.StoryCover
import com.shimmermare.stuffiread.stories.file.StoryFile

data class ImportedStory(
    val story: Story,
    val cover: StoryCover?,
    val files: List<StoryFile>
)
