package com.shimmermare.stuffiread.stories

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class CachedStoryService(
    private val source: StoryService
) : StoryService {
    private val cache: Cache<StoryId, Story> = Cache.Builder()
        .maximumCacheSize(CACHE_SIZE)
        .expireAfterAccess(EXPIRE_AFTER)
        .build()

    override suspend fun getStoryById(storyId: StoryId): Story? {
        var story = cache.get(storyId)
        if (story != null) return story

        story = source.getStoryById(storyId)
        if (story != null) cache.put(storyId, story)
        return story
    }

    override suspend fun getStoryPrequelIds(storyId: StoryId, ignoreInvalid: Boolean): Flow<StoryId> {
        return source.getStoryPrequelIds(storyId, ignoreInvalid)
    }

    /**
     * Return stories present in cache and retrieve missing ones from source.
     */
    override suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean): Flow<Story> {
        if (storyIds.isEmpty()) return emptyFlow()

        val storyIdsSet = if (storyIds is Set<StoryId>) storyIds else storyIds.toSet()

        val fromCache = storyIdsSet.mapNotNull { cache.get(it) }
        val fromCacheIds = fromCache.mapTo(mutableSetOf()) { it.id }

        val fromSource = source.getStoriesByIds((storyIdsSet - fromCacheIds), ignoreInvalid)
            .onEach { cache.put(it.id, it) }

        return merge(fromCache.asFlow(), fromSource)
    }

    override suspend fun getAllStories(ignoreInvalid: Boolean): Flow<Story> {
        return source.getAllStories(ignoreInvalid).onEach { cache.put(it.id, it) }
    }

    override suspend fun createStory(story: Story): Story {
        val created = source.createStory(story)
        cache.put(created.id, created)
        return created
    }

    override suspend fun updateStory(story: Story): Story {
        val updated = source.updateStory(story)
        cache.put(updated.id, updated)
        return updated
    }

    override suspend fun deleteStoryById(storyId: StoryId) {
        source.deleteStoryById(storyId)
        cache.invalidate(storyId)
    }

    companion object {
        private const val CACHE_SIZE: Long = 1000
        private val EXPIRE_AFTER: Duration = 10.minutes
    }
}