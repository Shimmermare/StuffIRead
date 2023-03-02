package com.shimmermare.stuffiread.ui.components.date

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

// TODO kotlinx.datetime does not support formatting yet, java has to be used
private val DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@Composable
fun Date(
    value: Instant,
    label: String = "",
    style: TextStyle = LocalTextStyle.current,
) {
    Date(label = label, value = value.toLocalDateTime(TimeZone.currentSystemDefault()), style = style)
}

@Composable
fun Date(
    value: LocalDateTime,
    label: String = "",
    style: TextStyle = LocalTextStyle.current
) {
    Text(text = label + " " + value.toJavaLocalDateTime().format(DEFAULT_FORMAT), style = style)
}