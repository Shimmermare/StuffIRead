package com.shimmermare.stuffiread.data.util

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object OffsetDateTimeColumnAdapter : ColumnAdapter<OffsetDateTime, Long> {
    override fun decode(databaseValue: Long): OffsetDateTime {
        return Instant.ofEpochSecond(databaseValue).atOffset(ZoneOffset.UTC)
    }

    override fun encode(value: OffsetDateTime): Long {
        return value.toEpochSecond()
    }
}