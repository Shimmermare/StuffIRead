package com.shimmermare.stuffiread.ui.components.input.datetime

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.DropdownPicker
import com.shimmermare.stuffiread.ui.components.input.ExtendedOutlinedTextField
import kotlinx.datetime.LocalTime

private val timesToPick = (0..23).map { LocalTime(it, 0) }

@Composable
fun TimePicker(
    value: LocalTime,
    showSeconds: Boolean = false,
    onValueChange: (LocalTime) -> Unit
) {
    DropdownPicker(
        value = value,
        onValueChange = onValueChange,
        displayText = { timeToDisplayString(it, showSeconds) },
        pickerField = { _ ->
            ExtendedOutlinedTextField(
                value = timeToDisplayString(value, showSeconds),
                modifier = Modifier.size(if (showSeconds) 130.dp else 105.dp, height = 36.dp),
                singleLine = true,
                // Because readOnly field consumes clicks (aka no open dropdown on click) - use disabled field
                enabled = false,
                // Change disabled field to use default text color
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(
                        LocalContentAlpha.current
                    )
                ),
                trailingIcon = { Icon(Icons.Filled.Schedule, null) },
            )
        },
        dropdownValues = timesToPick,
        dropdownContentModifier = Modifier.width(IntrinsicSize.Max).heightIn(max = 320.dp)
    )
}

private fun timeToDisplayString(value: LocalTime, includeSeconds: Boolean): String {
    val hours = value.hour.toString().padStart(2, '0')
    val minutes = value.minute.toString().padStart(2, '0')
    return if (includeSeconds) {
        val seconds = value.second.toString().padStart(2, '0')
        "$hours:$minutes:$seconds"
    } else {
        "$hours:$minutes"
    }
}