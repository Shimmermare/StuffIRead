package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagCategoryDescription.Companion.MAX_LENGTH
import com.shimmermare.stuffiread.ui.util.ComparatorUtils
import kotlinx.serialization.Serializable

/**
 * Represents tag category description.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagCategoryDescription private constructor(val value: String?) : Comparable<TagCategoryDescription> {
    val isPresent: Boolean get() = value != null

    init {
        if (value != null) {
            require(value.length <= MAX_LENGTH) { "Description length exceeded $MAX_LENGTH (${value.length})" }
        }
    }

    override fun compareTo(other: TagCategoryDescription): Int =
        ComparatorUtils.naturalOrderNullsLast<String>().compare(value, other.value)

    override fun toString(): String = value ?: ""

    companion object {
        val NONE = TagCategoryDescription(null)

        const val MAX_LENGTH = 2000

        fun of(description: String?) = if (description == null) NONE else TagCategoryDescription(description)
    }
}