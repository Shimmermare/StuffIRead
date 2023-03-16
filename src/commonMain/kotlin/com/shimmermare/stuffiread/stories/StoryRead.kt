package com.shimmermare.stuffiread.stories

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents an act of reading a story.
 */
@JvmInline
@Serializable
value class StoryRead(
    val date: Instant
) : Comparable<StoryRead> {
    override fun compareTo(other: StoryRead): Int {
        return date.compareTo(other.date)
    }
}
