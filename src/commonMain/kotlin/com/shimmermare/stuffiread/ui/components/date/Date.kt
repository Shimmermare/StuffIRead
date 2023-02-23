package com.shimmermare.stuffiread.ui.components.date

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

// TODO kotlinx.datetime does not support formatting yet, java has to be used
private val DEFAULT_FORMAT = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")

@Composable
fun Date(
    value: Instant,
) {
    Date(localDateTime = value.toLocalDateTime(TimeZone.currentSystemDefault()))
}

@Composable
fun Date(
    localDateTime: LocalDateTime
) {
    Text(text = localDateTime.toJavaLocalDateTime().format(DEFAULT_FORMAT))
}