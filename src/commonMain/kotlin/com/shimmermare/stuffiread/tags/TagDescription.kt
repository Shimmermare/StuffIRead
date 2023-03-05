package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.serialization.Serializable

/**
 * Represents tag description.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagDescription private constructor(val value: String?) : Comparable<TagDescription> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "Description length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: TagDescription): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = TagDescription(null)

        const val MAX_LENGTH = 2000

        fun of(name: String?) = if (name == null) NONE else TagDescription(name)
    }
}