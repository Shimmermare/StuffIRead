package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.tags.TagId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class StoryFilter(
    val idIn: Set<StoryId>? = null,
    /**
     * Case-insensitive.
     */
    val nameContains: String? = null,
    /**
     * Case-insensitive.
     * Will exclude stories without [Story.author].
     */
    val authorContains: String? = null,
    /**
     * Case-insensitive.
     * Will exclude stories without [Story.description].
     */
    val descriptionContains: String? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.published].
     */
    val publishedAfter: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.published].
     */
    val publishedBefore: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.changed].
     */
    val changedAfter: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.changed].
     */
    val changedBefore: Instant? = null,
    /**
     * Includes both explicit and implied tags.
     */
    val tagsPresent: Set<TagId>? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.score].
     */
    val scoreGreaterOrEqual: Score? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.score].
     */
    val scoreLessOrEqual: Score? = null,
    /**
     * Case-insensitive.
     * Will exclude stories without [Story.review].
     */
    val reviewContains: String? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.firstRead].
     */
    val firstReadAfter: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.firstRead].
     */
    val firstReadBefore: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.lastRead].
     */
    val lastReadAfter: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.lastRead].
     */
    val lastReadBefore: Instant? = null,
    /**
     * Inclusive.
     */
    val timesReadGreaterOrEqual: Int? = null,
    /**
     * Inclusive.
     */
    val timesReadLessOrEqual: Int? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.created].
     */
    val createdAfter: Instant? = null,
    /**
     * Inclusive.
     * Will exclude stories without [Story.created].
     */
    val createdBefore: Instant? = null,
    /**
     * Inclusive.
     */
    val updatedAfter: Instant? = null,
    /**
     * Inclusive.
     */
    val updatedBefore: Instant? = null,
) {
    init {
        require(publishedAfter == null || publishedBefore == null || publishedAfter <= publishedBefore) {
            "Published date filter has invalid range ($publishedBefore > $publishedAfter)"
        }
        require(changedAfter == null || changedBefore == null || changedAfter <= changedBefore) {
            "Changed date filter has invalid range ($changedBefore > $changedAfter)"
        }
        require(scoreGreaterOrEqual == null || scoreLessOrEqual == null || scoreGreaterOrEqual <= scoreLessOrEqual) {
            "Score filter has invalid range ($scoreGreaterOrEqual > $scoreLessOrEqual)"
        }
        require(firstReadAfter == null || firstReadBefore == null || firstReadAfter <= firstReadBefore) {
            "First read date filter has invalid range ($firstReadBefore > $firstReadAfter)"
        }
        require(lastReadAfter == null || lastReadBefore == null || lastReadAfter <= lastReadBefore) {
            "Last read date filter has invalid range ($lastReadBefore > $lastReadAfter)"
        }
        require(timesReadGreaterOrEqual == null || timesReadLessOrEqual == null || timesReadGreaterOrEqual <= timesReadLessOrEqual) {
            "Times read filter has invalid range ($timesReadGreaterOrEqual > $timesReadLessOrEqual)"
        }
        require(createdAfter == null || createdBefore == null || createdAfter <= createdBefore) {
            "Created date filter has invalid range ($createdBefore > $createdAfter)"
        }
        require(updatedAfter == null || updatedBefore == null || updatedAfter <= updatedBefore) {
            "updated date filter has invalid range ($updatedBefore > $updatedAfter)"
        }
    }

    companion object {
        val DEFAULT = StoryFilter()
    }
}
