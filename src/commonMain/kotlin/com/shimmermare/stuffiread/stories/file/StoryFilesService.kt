package com.shimmermare.stuffiread.stories.file

import com.shimmermare.stuffiread.stories.StoryId

interface StoryFilesService {
    suspend fun getStoryFilesMeta(storyId: StoryId): List<StoryFileMeta>

    suspend fun getStoryFiles(storyId: StoryId): List<StoryFile>

    /**
     * Set story files to [files].
     * Will delete files not in [files]!
     */
    suspend fun updateStoryFiles(storyId: StoryId, files: List<StoryFile>)
}