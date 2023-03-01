package com.shimmermare.stuffiread.stories

interface StoryService {
    suspend fun getStoryById(storyId: StoryId): Story?

    suspend fun getStoryByIdOrThrow(storyId: StoryId) = getStoryById(storyId) ?: error("Story $storyId not found")

    /**
     * @return only existing stories with IDs from [storyIds].
     */
    suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean = false): List<Story>

    suspend fun getAllStories(ignoreInvalid: Boolean = false): List<Story>

    /**
     * [Story.id] should be 0, next free ID will be automatically set.
     */
    suspend fun createStory(story: Story): Story

    suspend fun updateStory(story: Story): Story

    suspend fun deleteStoryById(storyId: StoryId)
}