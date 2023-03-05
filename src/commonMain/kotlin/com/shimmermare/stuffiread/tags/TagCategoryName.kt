package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagCategoryName.Companion.MAX_LENGTH
import kotlinx.serialization.Serializable

/**
 * Represents unique tag category name.
 * Can't be blank and has max length [MAX_LENGTH].
 */
@JvmInline
@Serializable
value class TagCategoryName(val value: String) : Comparable<TagCategoryName> {
    init {
        require(value.isNotBlank()) { "Name can't be blank" }
        require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
    }

    override fun compareTo(other: TagCategoryName): Int = value.compareTo(other.value)

    override fun toString(): String = value

    companion object {
        const val MAX_LENGTH = 100
    }
}