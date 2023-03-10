package com.shimmermare.stuffiread.ui.components.input.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime

@Composable
fun DateTimePicker(
    value: LocalDateTime,
    showSeconds: Boolean = false,
    onValueChange: (LocalDateTime) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        DatePicker(value.date, onValueChange = { onValueChange(it.atTime(value.time)) })
        TimePicker(value.time, showSeconds, onValueChange = { onValueChange(value.date.atTime(it)) })
    }
}