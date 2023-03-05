package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagId.Companion.None
import kotlinx.serialization.Serializable

/**
 * Represents tag ID.
 * Value of 0 is considered null-value for non-existing tags. See [None].
 */
@JvmInline
@Serializable
value class TagId(val value: UInt) : Comparable<TagId> {
    override fun compareTo(other: TagId): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    companion object {
        val None = TagId(0u)
    }
}