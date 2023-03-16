package com.shimmermare.stuffiread.stories

import kotlinx.coroutines.flow.Flow

interface StoryService {
    suspend fun getStoryById(storyId: StoryId): Story?

    suspend fun getStoryByIdOrThrow(storyId: StoryId) = getStoryById(storyId) ?: error("Story $storyId not found")

    suspend fun getStoryPrequelIds(storyId: StoryId, ignoreInvalid: Boolean = false): Flow<StoryId>

    /**
     * @return only existing stories with IDs from [storyIds].
     */
    suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean = false): Flow<Story>

    suspend fun getAllStories(ignoreInvalid: Boolean = false): Flow<Story>

    /**
     * [Story.id] should be 0, next free ID will be automatically set.
     */
    suspend fun createStory(story: Story): Story

    suspend fun updateStory(story: Story): Story

    suspend fun deleteStoryById(storyId: StoryId)
}