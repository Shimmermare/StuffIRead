package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.stories.file.StoryFilesService
import com.shimmermare.stuffiread.tags.TagService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter

class StorySearchServiceImpl(
    private val storyService: StoryService,
    private val storyFilesService: StoryFilesService,
    private val tagService: TagService,
) : StorySearchService {
    override suspend fun getStoriesByFilter(filter: StoryFilter, ignoreInvalidStories: Boolean): Flow<Story> {
        val stories = (if (filter.idIn != null) {
            storyService.getStoriesByIds(filter.idIn, ignoreInvalidStories)
        } else {
            storyService.getAllStories(ignoreInvalidStories)
        }).buffer()

        return stories.filterTextContains(filter.nameContains) { it.name.value }
            .filterTextContains(filter.authorContains) { it.author.value }
            .filterTextContains(filter.descriptionContains) { it.description.value }
            .filterTextContains(filter.urlContains) { it.url.value }
            .filterRange(filter.publishedAfter, filter.publishedBefore) { it.published }
            .filterRange(filter.changedAfter, filter.changedBefore) { it.changed }
            .filterRange(filter.scoreGreaterOrEqual, filter.scoreLessOrEqual) { it.score }
            .filterTextContains(filter.reviewContains) { it.review.value }
            .filterRange(filter.firstReadAfter, filter.firstReadBefore) { it.firstRead }
            .filterRange(filter.lastReadAfter, filter.lastReadBefore) { it.lastRead }
            .filterRange(filter.timesReadGreaterOrEqual, filter.timesReadLessOrEqual) { it.reads.size.toUInt() }
            .filterRange(filter.createdAfter, filter.createdBefore) { it.created }
            .filterRange(filter.updatedAfter, filter.updatedBefore) { it.updated }
            .filterOnFiles(filter)
            .let {
                if (filter.tagsPresent != null) {
                    it.filter { story ->
                        if (story.tags.isEmpty()) return@filter false
                        if (story.tags.containsAll(filter.tagsPresent)) return@filter true

                        // Is this too costly?
                        val storyTagsWithImplicit = tagService.getAllTagIdsByExplicitTagIds(story.tags)
                        return@filter storyTagsWithImplicit.containsAll(filter.tagsPresent)
                    }
                } else {
                    it
                }
            }
            .let {
                if (filter.tagsAbsent != null) {
                    it.filter { story ->
                        if (story.tags.isEmpty()) return@filter true
                        if (filter.tagsAbsent.any { story.tags.contains(it) }) return@filter false

                        // Is this too costly?
                        val storyTagsWithImplicit = tagService.getAllTagIdsByExplicitTagIds(story.tags)
                        return@filter filter.tagsAbsent.none { storyTagsWithImplicit.contains(it) }
                    }
                } else {
                    it
                }
            }
            .let {
                if (filter.isPrequelOf != null) {
                    it.filter { story -> filter.isPrequelOf.any { story.sequels.contains(it) } }
                } else {
                    it
                }
            }
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

    private fun Flow<Story>.filterOnFiles(filter: StoryFilter): Flow<Story> {
        val filterOnFileMeta = filter.wordCountGreaterOrEqual != null || filter.wordCountLessOrEqual != null
        val filterOnFileContent = filter.contentContains != null

        if (!filterOnFileMeta && !filterOnFileContent) return this

        return filter { story ->
            val files: List<StoryFile>? = if (filterOnFileContent) {
                storyFilesService.getStoryFiles(story.id)
            } else {
                null
            }
            val filesMeta: List<StoryFileMeta>? = if (filterOnFileMeta) {
                files?.map { it.meta } ?: storyFilesService.getStoryFilesMeta(story.id)
            } else {
                null
            }

            if (filter.wordCountGreaterOrEqual != null || filter.wordCountLessOrEqual != null) {
                val totalWordCount = filesMeta!!.sumOf { it.wordCount }
                filter.wordCountGreaterOrEqual?.let {
                    if (totalWordCount < it) return@filter false
                }
                filter.wordCountLessOrEqual?.let {
                    if (totalWordCount > it) return@filter false
                }
            }

            if (filter.contentContains != null) {
                val contains = files!!.any {
                    when (it.meta.format) {
                        StoryFileFormat.TXT, StoryFileFormat.HTML -> {
                            val contentText = String(it.content, Charsets.UTF_8)
                            contentText.contains(filter.contentContains, ignoreCase = true)
                        }

                        else -> false
                    }
                }
                if (!contains) return@filter false
            }
            true
        }
    }
}