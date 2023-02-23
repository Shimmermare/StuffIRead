package com.shimmermare.stuffiread.stories

import io.github.reactivecircus.cache4k.Cache
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

    /**
     * Return stories present in cache and retrieve missing ones from source.
     */
    override suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean): List<Story> {
        if (storyIds.isEmpty()) return emptyList()

        val storyIdsSet = if (storyIds is Set<StoryId>) storyIds else storyIds.toSet()

        val fromCache = storyIdsSet.mapNotNull { cache.get(it) }
        val fromCacheIds = fromCache.mapTo(mutableSetOf()) { it.id }

        val fromSource = source.getStoriesByIds((storyIdsSet - fromCacheIds), ignoreInvalid)
        fromCache.forEach { cache.put(it.id, it) }

        return fromCache + fromSource
    }

    override suspend fun getAllStories(ignoreInvalid: Boolean): List<Story> {
        return source.getAllStories(ignoreInvalid).onEach {
            cache.put(it.id, it)
        }
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

    companion object {
        private const val CACHE_SIZE: Long = 1000
        private val EXPIRE_AFTER: Duration = 10.minutes
    }
}