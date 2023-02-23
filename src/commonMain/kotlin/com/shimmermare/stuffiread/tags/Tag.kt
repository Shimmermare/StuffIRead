package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.tags.TagName.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias TagId = Int

/**
 * Represents discrete characteristic about the story.
 */
@Serializable
data class Tag(
    val id: TagId = 0,
    /**
     * Unique displayed name.
     */
    val name: TagName,
    /**
     * @see TagCategory
     */
    val categoryId: TagCategoryId,
    /**
     * Displayed optional tag description.
     */
    val description: TagDescription = TagDescription.NONE,

    /**
     * Tags that are implied to also be present when this tag is present.
     * Note: implied tags are allowed to form implication cycles. That just means that all tags in cycle are implied.
     */
    val impliedTagIds: Set<TagId> = emptySet(),
    val created: Instant = Instant.fromEpochSeconds(0),
    val updated: Instant = created,
) {
    init {
        if (impliedTagIds.contains(id)) {
            throw IllegalArgumentException("Tag can't imply itself")
        }
        if (updated < created) {
            throw IllegalArgumentException("Updated date ($updated) is before created date ($created)")
        }
    }
}

/**
 * Represents unique tag name.
 * Can't be blank, has max length [MAX_LENGTH] and not allowed to be multi-line.
 */
@JvmInline
@Serializable
value class TagName(val value: String) : Comparable<TagName> {
    init {
        require(value.isNotBlank()) { "Name can't be blank" }
        require(!value.contains('\n')) { "Multi-line name is not allowed" }
        require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
    }

    override fun compareTo(other: TagName): Int = value.compareTo(other.value)

    override fun toString(): String = value

    companion object {
        const val MAX_LENGTH = 100
    }
}

/**
 * Represents tag description.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagDescription private constructor(val value: String?) : Comparable<TagDescription> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.isNotBlank()) { "Name can't be blank" }
            require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: TagDescription): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = TagDescription(null)

        const val MAX_LENGTH = 2000

        fun of(name: String?) = if (name == null) NONE else TagDescription(name)
    }
}
