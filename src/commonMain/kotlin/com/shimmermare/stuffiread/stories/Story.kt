package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryAuthor.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.stories.StoryDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.stories.StoryId.Companion.None
import com.shimmermare.stuffiread.stories.StoryName.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.stories.StoryReview.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.stories.StoryURL.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.tags.TagCategoryDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

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
}

/**
 * Represents story ID.
 * Value of 0 is considered null-value for non-existing stories. See [None].
 */
@JvmInline
@Serializable
value class StoryId(val value: UInt) : Comparable<StoryId> {
    override fun compareTo(other: StoryId): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        val None = StoryId(0u)
    }
}

/**
 * Represents story author's name/nickname.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryAuthor private constructor(val value: String?) : Comparable<StoryAuthor> {
    init {
        if (value != null) {
            require(value.isNotBlank()) { "Author name can't be blank" }
            require(!value.contains('\n')) { "Multi-line author name is not allowed" }
            require(value.length <= MAX_LENGTH) { "Author name length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    val isPresent: Boolean get() = value != null

    override fun compareTo(other: StoryAuthor): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: "Unknown Author"

    companion object {
        val UNKNOWN = StoryAuthor(null)

        const val MAX_LENGTH = 120

        fun of(author: String?) = if (author.isNullOrBlank()) UNKNOWN else StoryAuthor(author)
    }
}

/**
 * Represents story name.
 * Optional, can't be blank or multi-line, has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryName(val value: String) : Comparable<StoryName> {
    init {
        require(value.isNotBlank()) { "Name can't be blank" }
        require(!value.contains('\n')) { "Multi-line name is not allowed" }
        require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
    }

    override fun compareTo(other: StoryName): Int = value.compareTo(other.value)

    override fun toString(): String = value

    companion object {
        const val MAX_LENGTH = 200
    }
}

/**
 * Represents story URL.
 * Has to be valid URL with max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryURL private constructor(val value: String?) : Comparable<StoryURL> {
    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "URL length exceeded $MAX_LENGTH (${value.length})" }
            Url(value)
        }
    }

    val isPresent: Boolean get() = value != null

    override fun compareTo(other: StoryURL): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = StoryURL(null)

        const val MAX_LENGTH = 200

        fun of(url: String?) = if (url.isNullOrBlank()) NONE else StoryURL(url)
    }
}

/**
 * Represents tag description.
 * Optional, has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryDescription private constructor(val value: String?) : Comparable<StoryDescription> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "Description length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: StoryDescription): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = StoryDescription(null)

        const val MAX_LENGTH = 2000

        fun of(description: String?) = if (description.isNullOrBlank()) NONE else StoryDescription(description)
    }
}

/**
 * Represents tag review.
 * Optional, has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryReview private constructor(val value: String?) : Comparable<StoryReview> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "Review length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: StoryReview): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = StoryReview(null)

        const val MAX_LENGTH = 2000

        fun of(review: String?) = if (review.isNullOrBlank()) NONE else StoryReview(review)
    }
}