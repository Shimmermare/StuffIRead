package com.shimmermare.stuffiread.stories

import kotlinx.serialization.Serializable

/**
 * Score normalized to [0, 1] range.
 * Should be mapped to more common range like 1-10 on display.
 */
@JvmInline
@Serializable
value class Score(
    val value: Float
) : Comparable<Score> {
    init {
        require(value in 0F..1F) {
            "Score is outside of normalized 0 to 1 range: $value"
        }
    }

    override fun compareTo(other: Score): Int {
        return value.compareTo(other.value)
    }
}
