package com.shimmermare.stuffiread.stories.cover

import com.shimmermare.stuffiread.stories.StoryId
import java.nio.file.Path

/**
 * Cover with be deleted when story is deleted, no need to delete it separately.
 */
interface StoryCoverService {
    suspend fun getStoryCover(storyId: StoryId): StoryCover?

    suspend fun updateStoryCover(storyId: StoryId, cover: StoryCover?)

    suspend fun loadCoverFile(path: Path): StoryCover

    companion object {

    }
}