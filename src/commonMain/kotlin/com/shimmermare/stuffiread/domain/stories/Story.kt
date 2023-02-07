package com.shimmermare.stuffiread.domain.stories

import com.shimmermare.stuffiread.domain.tags.TagId
import java.time.OffsetDateTime

typealias StoryId = Int

/**
 * Story entity is centerpiece of the app.
 *
 * Primary properties such as name are stored individually, and any additional properties such as language
 * always can be added as tags.
 */
data class Story(
    val id: StoryId = 0,
    /**
     * Story author.
     * Optional: if not set - author either unknown or anonymous.
     */
    val author: String? = null,
    /**
     * Story name as given.
     * Non-unique: multiple stories can have the same name.
     */
    val name: String,
    /**
     * Optional primary source URL. If multiple sources present - usually should be set to
     */
    val url: String? = null,
    /**
     * Optional story description.
     */
    val description: String? = null,
    /**
     * Date when story was originally created, NOT date when it was added here.
     * Optional: can be absent if unknown.
     */
    val created: OffsetDateTime? = null,
    /**
     * Date when story was edited last time. For finished stories this is usually the completion date.
     * Optional: can be absent if unknown.
     */
    val updated: OffsetDateTime? = created,
    /**
     * Tags that are explicitly added to story. DOES NOT include implied tags.
     */
    val tags: Set<TagId> = emptySet(),
    /**
     * Stories that are SEQUELS to this story. These sequels will have this story present in [prequels].
     * Cycles are allowed.
     */
    val sequels: Set<StoryId> = emptySet(),
    /**
     * Stories that are PREQUELS to this story. These prequels will have this story present in [sequels].
     * Cycles are allowed.
     */
    val prequels: Set<StoryId> = emptySet(),
    /**
     * All files associated with this story.
     */
    val files: Set<StoryFileId> = emptySet(),
    /**
     * User score.
     */
    val score: Score? = null,
    /**
     * User review and notes.
     */
    val review: String? = null,
    /**
     * When user did read this story the first time.
     */
    val firstRead: OffsetDateTime? = null,
    /**
     * When user did read this story the last time.
     */
    val lastRead: OffsetDateTime? = firstRead,
    /**
     * How many times user did read this story.
     * Implied to include only "full" reads, but interpretation is up to user.
     */
    val timesRead: Int = 1,
) {
    init {
        if (author != null && author.length > MAX_AUTHOR_LENGTH) {
            throw IllegalArgumentException("Author length exceeded $MAX_AUTHOR_LENGTH (${author.length})")
        }
        if (name.length > MAX_NAME_LENGTH) {
            throw IllegalArgumentException("Name length exceeded $MAX_NAME_LENGTH (${name.length})")
        }
        if (url != null && url.length > MAX_URL_LENGTH) {
            throw IllegalArgumentException("URL length exceeded $MAX_URL_LENGTH (${url.length})")
        }
        if (description != null && description.length > MAX_DESCRIPTION_LENGTH) {
            throw IllegalArgumentException("Description length exceeded $MAX_DESCRIPTION_LENGTH (${description.length})")
        }
        if (created != null && updated != null && updated.isBefore(created)) {
            throw IllegalArgumentException("Updated date ($updated) is before created date ($created)")
        }
        if (sequels.contains(id)) {
            throw IllegalArgumentException("Story can't be sequel to itself")
        }
        if (prequels.contains(id)) {
            throw IllegalArgumentException("Story can't be prequel to itself")
        }
        if (review != null && review.length > MAX_REVIEW_LENGTH) {
            throw IllegalArgumentException("Review length exceeded $MAX_REVIEW_LENGTH (${review.length})")
        }
        if (firstRead != null && lastRead != null && lastRead.isBefore(firstRead)) {
            throw IllegalArgumentException("Last read date ($lastRead) is before first read date ($firstRead)")
        }
        if (timesRead < 0) {
            throw IllegalArgumentException("Times read negative ($timesRead)")
        }
    }

    companion object {
        const val MAX_AUTHOR_LENGTH = 1000
        const val MAX_NAME_LENGTH = 1000
        const val MAX_URL_LENGTH = 1000
        const val MAX_DESCRIPTION_LENGTH = 20000
        const val MAX_REVIEW_LENGTH = 20000

    }
}