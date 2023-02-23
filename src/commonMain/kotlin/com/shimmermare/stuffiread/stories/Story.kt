package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.tags.TagId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias StoryId = UInt

/**
 * Story entity is centerpiece of the app.
 *
 * Primary properties such as name are stored individually, and any additional properties such as language
 * always can be added as tags.
 */
@Serializable
data class Story(
    val id: StoryId = 0u,
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
     * Date when story was originally published, NOT date when it was added here.
     * Optional: can be absent if unknown.
     */
    val published: Instant? = null,
    /**
     * Date when story was changed last time. For finished stories this is usually the completion date.
     * Optional: can be absent if unknown.
     */
    val changed: Instant? = published,
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
    val files: List<StoryFile> = emptyList(),
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
    val firstRead: Instant? = null,
    /**
     * When user did read this story the last time.
     */
    val lastRead: Instant? = firstRead,
    /**
     * How many times user did read this story.
     * Implied to include only "full" reads, but interpretation is up to user.
     */
    val timesRead: Int = 1,
    /**
     * Date when story entry was created in archive.
     */
    val created: Instant,
    /**
     * Date when story entry was last updated in archive.
     */
    val updated: Instant = created,
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
        if (published != null && changed != null && changed < published) {
            throw IllegalArgumentException("Changed date ($changed) is before published date ($published)")
        }
        if (review != null && review.length > MAX_REVIEW_LENGTH) {
            throw IllegalArgumentException("Review length exceeded $MAX_REVIEW_LENGTH (${review.length})")
        }
        if (firstRead != null && lastRead != null && lastRead < firstRead) {
            throw IllegalArgumentException("Last read date ($lastRead) is before first read date ($firstRead)")
        }
        if (timesRead < 0) {
            throw IllegalArgumentException("Times read negative ($timesRead)")
        }
        if (updated < created) {
            throw IllegalArgumentException("Updated date ($updated) is before created date ($created)")
        }
    }

    companion object {
        const val MAX_AUTHOR_LENGTH = 120
        const val MAX_NAME_LENGTH = 1000
        const val MAX_URL_LENGTH = 1000
        const val MAX_DESCRIPTION_LENGTH = 10000
        const val MAX_REVIEW_LENGTH = 10000
    }
}