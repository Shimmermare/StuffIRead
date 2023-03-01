package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.tags.TagService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter

class StorySearchServiceImpl(
    private val storyService: StoryService,
    private val tagService: TagService,
) : StorySearchService {
    override suspend fun getStoriesByFilter(filter: StoryFilter, ignoreInvalidStories: Boolean): Flow<Story> {
        var stories = (if (filter.idIn != null) {
            storyService.getStoriesByIds(filter.idIn, ignoreInvalidStories)
        } else {
            storyService.getAllStories(ignoreInvalidStories)
        }).buffer()

        stories = stories.filterTextContains(filter.nameContains) { it.name.value }
        stories = stories.filterTextContains(filter.authorContains) { it.author.value }
        stories = stories.filterTextContains(filter.descriptionContains) { it.description.value }
        stories = stories.filterRange(filter.publishedAfter, filter.publishedBefore) { it.published }
        stories = stories.filterRange(filter.changedAfter, filter.changedBefore) { it.changed }

        if (filter.tagsPresent != null) {
            stories = stories.filter {
                if (it.tags.isEmpty()) return@filter false
                if (it.tags.containsAll(filter.tagsPresent)) return@filter true

                // Is this too costly?
                val storyTagsWithImplicit = tagService.getAllTagIdsByExplicitTagIds(it.tags)
                return@filter storyTagsWithImplicit.containsAll(filter.tagsPresent)
            }
        }

        stories = stories.filterRange(filter.scoreGreaterOrEqual, filter.scoreLessOrEqual) { it.score }
        stories = stories.filterTextContains(filter.reviewContains) { it.review.value }
        stories = stories.filterRange(filter.firstReadAfter, filter.firstReadBefore) { it.firstRead }
        stories = stories.filterRange(filter.lastReadAfter, filter.lastReadBefore) { it.lastRead }
        stories = stories.filterRange(filter.timesReadGreaterOrEqual, filter.timesReadLessOrEqual) { it.timesRead }
        stories = stories.filterRange(filter.createdAfter, filter.createdBefore) { it.created }
        stories = stories.filterRange(filter.updatedAfter, filter.updatedBefore) { it.updated }

        return stories
    }

    private inline fun Flow<Story>.filterTextContains(
        substring: String?,
        ignoreCase: Boolean = true,
        crossinline getter: (Story) -> String?
    ): Flow<Story> {
        return if (substring != null) {
            filter {
                val text = getter(it)
                text != null && text.contains(substring, ignoreCase = ignoreCase)
            }
        } else {
            this
        }
    }

    private inline fun <T : Comparable<T>> Flow<Story>.filterRange(
        fromInclusive: T?,
        toInclusive: T?,
        crossinline getter: (Story) -> T?
    ): Flow<Story> {
        return when {
            fromInclusive != null && toInclusive != null -> {
                filter {
                    val date = getter(it)
                    date != null && date >= fromInclusive && date <= toInclusive
                }
            }

            fromInclusive != null -> {
                filter {
                    val date = getter(it)
                    date != null && date >= fromInclusive
                }
            }

            toInclusive != null -> {
                filter {
                    val date = getter(it)
                    date != null && date <= toInclusive
                }
            }

            else -> this
        }
    }
}