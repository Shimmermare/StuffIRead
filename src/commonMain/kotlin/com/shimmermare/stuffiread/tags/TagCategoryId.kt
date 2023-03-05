package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagCategoryId.Companion.None
import kotlinx.serialization.Serializable

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