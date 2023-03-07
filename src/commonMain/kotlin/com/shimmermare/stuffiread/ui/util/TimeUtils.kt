package com.shimmermare.stuffiread.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object TimeUtils {
    val EPOCH_START = Instant.fromEpochSeconds(0)

    fun instantAtToday1200(): Instant {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.atTime(12, 0)
            .toInstant(TimeZone.currentSystemDefault())
    }
}