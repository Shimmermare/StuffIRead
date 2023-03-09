package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun <T> CheckboxListField(
    value: Map<T, Boolean>,
    displayNameProvider: (T) -> String = { it.toString() },
    onValueChange: (Map<T, Boolean>) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        value.forEach { (flag, checked) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onValueChange(value + (flag to it)) }
                )
                Text(displayNameProvider(flag))
            }
        }
    }
}