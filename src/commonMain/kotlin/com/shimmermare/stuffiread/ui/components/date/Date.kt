package com.shimmermare.stuffiread.ui.components.date

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DEFAULT_FORMAT = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")

@Composable
fun Date(
    value: OffsetDateTime,
    displayInLocalTime: Boolean = true,
) {
    val text = if (displayInLocalTime) {
        value.atZoneSameInstant(ZoneId.systemDefault()).format(DEFAULT_FORMAT)
    } else {
        value.format(DEFAULT_FORMAT) + " (UTC)"
    }
    Text(text)
}