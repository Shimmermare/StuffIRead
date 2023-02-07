package com.shimmermare.stuffiread.data.stories

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.domain.stories.Score
import com.shimmermare.stuffiread.domain.stories.Story
import com.shimmermare.stuffiread.domain.stories.StoryFileId
import com.shimmermare.stuffiread.domain.stories.StoryId
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.data.stories.Story as DbStory

class StoryDatasourceImpl(
    private val db: Database
) : StoryDatasource {
    private val storyQueries: StoryQueries = db.storyQueries
    private val storyTagQueries: StoryTagQueries = db.storyTagQueries
    private val storySequelQueries: StorySequelQueries = db.storySequelQueries
    private val storyFileQueries: StoryFileQueries = db.storyFileQueries

    override fun findAll(): List<Story> {
        return db.transactionWithResult {
            val stories: List<DbStory> = storyQueries.selectAll().executeAsList()
            val storyIds: Set<StoryId> = stories.map { it.id }.toSet()
            val tagsByStoryId: Map<StoryId, Set<TagId>> = storyTagQueries.selectTagsForStories(storyIds)
                .executeAsList()
                .groupBy({ it.story }) { it.tag }.mapValues { (_, v) -> v.toSet() }
            val sequelsByStoryId: Map<StoryId, Set<StoryId>> = storySequelQueries.selectSequelsForStories(storyIds)
                .executeAsList()
                .groupBy({ it.story }) { it.sequel }.mapValues { (_, v) -> v.toSet() }
            val prequelsByStoryId: Map<StoryId, Set<StoryId>> = storySequelQueries.selectPrequelsForStories(storyIds)
                .executeAsList()
                .groupBy({ it.story }) { it.prequel }.mapValues { (_, v) -> v.toSet() }
            val filesByStoryId: Map<StoryId, Set<StoryFileId>> = storyFileQueries.selectFileIdsForStories(storyIds)
                .executeAsList()
                .groupBy({ it.story }) { it.id }.mapValues { (_, v) -> v.toSet() }

            return@transactionWithResult stories.map {
                toEntity(
                    it,
                    tagsByStoryId.getOrDefault(it.id, emptySet()),
                    sequelsByStoryId.getOrDefault(it.id, emptySet()),
                    prequelsByStoryId.getOrDefault(it.id, emptySet()),
                    filesByStoryId.getOrDefault(it.id, emptySet())
                )
            }
        }
    }

    private fun toEntity(
        story: DbStory,
        tags: Set<TagId>,
        sequels: Set<StoryId>,
        prequels: Set<StoryId>,
        files: Set<StoryFileId>,
    ): Story {
        return Story(
            story.id,
            story.author,
            story.name,
            story.url,
            story.description,
            story.createdTs,
            story.updatedTs,
            tags,
            sequels,
            prequels,
            files,
            story.score?.let { Score(it) },
            story.review,
            story.firstReadTs,
            story.lastReadTs,
            story.timesRead
        )
    }
}