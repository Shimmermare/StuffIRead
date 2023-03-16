package com.shimmermare.stuffiread.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object TimeUtils {
    val EPOCH_START = Instant.fromEpochSeconds(0)

    fun instantTodayAt0000(): Instant {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.atTime(0, 0)
            .toInstant(TimeZone.currentSystemDefault())
    }

    fun instantTodayAt1200(): Instant {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.atTime(12, 0)
            .toInstant(TimeZone.currentSystemDefault())
    }

    fun todayAt1200(): LocalDateTime {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.atTime(12, 0)
    }
}