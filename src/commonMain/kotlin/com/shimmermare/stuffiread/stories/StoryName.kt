package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryName.Companion.MAX_LENGTH
import kotlinx.serialization.Serializable

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