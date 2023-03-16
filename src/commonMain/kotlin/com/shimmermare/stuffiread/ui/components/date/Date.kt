package com.shimmermare.stuffiread.ui.components.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
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
    style: TextStyle = LocalTextStyle.current,
) {
    Date(value = value.toLocalDateTime(TimeZone.currentSystemDefault()), style = style)
}

@Composable
fun Date(
    value: LocalDateTime,
    style: TextStyle = LocalTextStyle.current
) {
    Text(text = value.toJavaLocalDateTime().format(DEFAULT_FORMAT), style = style, maxLines = 1)
}

@Composable
fun DateWithLabel(
    label: String,
    value: Instant,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(label, maxLines = 1)
        Date(value, style)
    }
}