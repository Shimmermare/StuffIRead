package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryId.Companion.None
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.util.TimeUtils
import com.shimmermare.stuffiread.util.AppJson
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import com.shimmermare.stuffiread.util.Migration
import com.shimmermare.stuffiread.util.repeat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.datetime.until
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

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
     * Times when user read the story.
     * Whenever that means only "full" reads or not is up to user.
     */
    val reads: List<StoryRead> = emptyList(),
    /**
     * Date when story entry was created in archive.
     */
    val created: Instant = TimeUtils.EPOCH_START,
    /**
     * Date when story entry was last updated in archive.
     */
    val updated: Instant = created,
) {
    val firstRead: Instant? by lazy { reads.minOfOrNull(StoryRead::date) }
    val lastRead: Instant? by lazy { reads.maxOfOrNull(StoryRead::date) }

    init {
        require(published == null || changed == null || changed >= published) {
            "Changed date ($changed) is before published date ($published)"
        }
        require(!sequels.contains(id)) {
            "Story can't be sequel to itself"
        }
        require(updated >= created) {
            "Updated date ($updated) is before created date ($created)"
        }
    }

    companion object {
        const val VERSION: UInt = 2u
    }
}

/**
 * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
 * Replace with [Serializer] when fixed.
 */
object StorySerializer : JsonVersionedSerializer<Story>(
    currentVersion = Story.VERSION,
    migrations = listOf(
        // Migrate from dates of first and last reads to individual reads
        Migration(2u) { element ->
            element as JsonObject

            val fields = element.toMutableMap()

            val timesRead = fields.remove("timesRead")?.jsonPrimitive?.long ?: 0L
            val firstRead = fields.remove("firstRead")?.let { AppJson.decodeFromJsonElement<Instant?>(it) }
            val lastRead = fields.remove("lastRead")?.let { AppJson.decodeFromJsonElement<Instant?>(it) }

            if (timesRead == 0L) {
                return@Migration JsonObject(fields)
            }

            val reads: List<StoryRead> = when {
                // Interpolate between firstRead and lastRead
                timesRead > 1L && firstRead != null && lastRead != null -> {
                    val secondsBetween = firstRead.until(lastRead, DateTimeUnit.SECOND)
                    (0 until timesRead).map { index ->
                        StoryRead(firstRead.plus(secondsBetween / (timesRead - 1) * index, DateTimeUnit.SECOND))
                    }
                }

                firstRead != null -> {
                    StoryRead(firstRead).repeat(timesRead)
                }

                lastRead != null -> {
                    StoryRead(lastRead).repeat(timesRead)
                }
                else -> {
                    // In case we don't have any timestamps - no other choice but to go for 1970.
                    StoryRead(Instant.fromEpochSeconds(0)).repeat(timesRead)
                }
            }

            fields["reads"] = AppJson.encodeToJsonElement(reads)

            JsonObject(fields)
        }
    ),
    actualSerializer = Story.serializer()
)