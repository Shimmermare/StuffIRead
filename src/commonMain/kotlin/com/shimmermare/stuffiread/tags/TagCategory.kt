package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagCategoryDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.tags.TagCategoryId.Companion.None
import com.shimmermare.stuffiread.tags.TagCategoryName.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.tags.TagName.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.awt.Color

/**
 * Tag categories are used to group tags by common properties.
 */
@Serializable
data class TagCategory(
    val id: TagCategoryId = None,
    /**
     * Unique displayed name.
     */
    val name: TagCategoryName,
    /**
     * Optional displayed description.
     */
    val description: TagCategoryDescription = TagCategoryDescription.NONE,
    /**
     * Order by which tags are sorted on display.
     */
    val sortOrder: Int = 0,
    /**
     * All tags of type will be displayed using this color.
     */
    val color: Int = DEFAULT_COLOR,
    val created: Instant = Instant.fromEpochSeconds(0),
    val updated: Instant = created,
) {
    init {
        require(updated >= created) {
            "Updated date ($updated) is before created date ($created)"
        }
    }

    companion object {
        val DEFAULT_COLOR: Int = Color(0, 180, 255).rgb

        val DEFAULT_ORDER: Comparator<TagCategory> = Comparator
            .comparing(TagCategory::sortOrder)
            .thenComparing(TagCategory::name)
            .thenComparing(TagCategory::id)
    }
}

/**
 * Represents tag category ID.
 * Value of 0 is considered null-value for non-existing tag categories. See [None].
 */
@JvmInline
@Serializable
value class TagCategoryId(val value: UInt) : Comparable<TagCategoryId> {
    override fun compareTo(other: TagCategoryId): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        val None = TagCategoryId(0u)
    }
}

/**
 * Represents unique tag category name.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagCategoryName(val value: String) : Comparable<TagCategoryName> {
    init {
        require(value.isNotBlank()) { "Name can't be blank" }
        require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
    }

    override fun compareTo(other: TagCategoryName): Int = value.compareTo(other.value)

    override fun toString(): String = value

    companion object {
        const val MAX_LENGTH = 100
    }
}

/**
 * Represents tag category description.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagCategoryDescription private constructor(val value: String?) : Comparable<TagCategoryDescription> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "Description length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: TagCategoryDescription): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = TagCategoryDescription(null)

        const val MAX_LENGTH = 2000

        fun of(description: String?) = if (description == null) NONE else TagCategoryDescription(description)
    }
}