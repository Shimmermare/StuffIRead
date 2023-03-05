package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryReview.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.serialization.Serializable

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