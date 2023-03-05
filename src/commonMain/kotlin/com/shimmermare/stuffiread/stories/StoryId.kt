package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryId.Companion.None
import kotlinx.serialization.Serializable

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