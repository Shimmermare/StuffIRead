package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.serialization.Serializable

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