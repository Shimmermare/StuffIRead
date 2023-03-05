package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryId.Companion.None
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

/**
 * Story entity is centerpiece of the app.
 *
 * Primary properties such as name are stored individually, and any additional properties such as language
 * always can be added as tags.
 */
@Serializable
data class Story(
    val id: StoryId = None,
    /**
     * Story author.
     * Optional: if not set - author either unknown or anonymous.
     */
    val author: StoryAuthor = StoryAuthor.UNKNOWN,
    /**
     * Story name as given.
     * Non-unique: multiple stories can have the same name.
     */
    val name: StoryName,
    /**
     * Optional primary source URL.
     * TODO: Migrate to native Kotlin Multiplatform URL when available.
     */
    val url: StoryURL = StoryURL.NONE,
    /**
     * Optional story description.
     */
    val description: StoryDescription = StoryDescription.NONE,
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
     * Stories that are SEQUELS to this story.
     * Cycles are allowed (but story can't be direct sequel to itself).
     */
    val sequels: Set<StoryId> = emptySet(),
    /**
     * User score.
     */
    val score: Score? = null,
    /**
     * User review and notes.
     */
    val review: StoryReview = StoryReview.NONE,
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
     * Whenever that means only "full" reads or not is up to user.
     */
    val timesRead: UInt = 1u,
    /**
     * Date when story entry was created in archive.
     */
    val created: Instant = Instant.fromEpochSeconds(0),
    /**
     * Date when story entry was last updated in archive.
     */
    val updated: Instant = created,
) {
    init {
        require(published == null || changed == null || changed >= published) {
            "Changed date ($changed) is before published date ($published)"
        }
        require(firstRead == null || lastRead == null || lastRead >= firstRead) {
            "Last read date ($lastRead) is before first read date ($firstRead)"
        }
        require(!sequels.contains(id)) {
            "Story can't be sequel to itself"
        }
        require(updated >= created) {
            "Updated date ($updated) is before created date ($created)"
        }
    }

    companion object {
        const val VERSION: UInt = 1u
    }
}

/**
 * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
 * Replace with [Serializer] when fixed.
 */
object StorySerializer : JsonVersionedSerializer<Story>(
    currentVersion = Story.VERSION,
    migrations = listOf(
        // Example:
        // Migration(1u) {
        //     JsonObject(it.jsonObject + ("newProperty" to JsonPrimitive("new value")))
        // }
    ),
    actualSerializer = Story.serializer()
)