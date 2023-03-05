package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryAuthor.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.serialization.Serializable

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