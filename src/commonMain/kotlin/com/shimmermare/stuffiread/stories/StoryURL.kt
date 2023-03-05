package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.StoryURL.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Represents story URL.
 * Has to be valid URL with max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class StoryURL private constructor(val value: String?) : Comparable<StoryURL> {
    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "URL length exceeded $MAX_LENGTH (${value.length})" }
            Url(value)
        }
    }

    val isPresent: Boolean get() = value != null

    override fun compareTo(other: StoryURL): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = StoryURL(null)

        const val MAX_LENGTH = 200

        fun of(url: String?) = if (url.isNullOrBlank()) NONE else StoryURL(url)
    }
}