package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagName.Companion.MAX_LENGTH
import kotlinx.serialization.Serializable

/**
 * Represents unique tag name.
 * Can't be blank, has max length [MAX_LENGTH] and not allowed to be multi-line.
 */
@JvmInline
@Serializable
value class TagName(val value: String) : Comparable<TagName> {
    init {
        require(value.isNotBlank()) { "Name can't be blank" }
        require(!value.contains('\n')) { "Multi-line name is not allowed" }
        require(value.length <= MAX_LENGTH) { "Name length exceeded $MAX_LENGTH (${value.length})" }
        require(!value.startsWith(' ') && !value.endsWith(' ')) { "Name can't have leading/trailing whitespaces" }
    }

    override fun compareTo(other: TagName): Int = value.compareTo(other.value)

    override fun toString(): String = value

    companion object {
        const val MAX_LENGTH = 100
    }
}