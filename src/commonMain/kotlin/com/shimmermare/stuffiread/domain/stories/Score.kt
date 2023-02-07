package com.shimmermare.stuffiread.domain.stories

import kotlin.math.max
import kotlin.math.min

/**
 * Score normalized to [0, 1] range.
 * Should be mapped to more common range like 1-10 on display.
 */
class Score(
    value: Float
) {
    val value: Float = min(0F, max(1F, value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Score

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
