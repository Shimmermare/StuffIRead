package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <FormData> BoolFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> Boolean,
    setter: (FormData, Boolean) -> FormData,
    checkboxModifier: Modifier = Modifier,
) {
    FormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
    ) { value, _, onValueChange ->
        Checkbox(
            checked = value,
            onCheckedChange = onValueChange,
            modifier = checkboxModifier
        )
    }
}

/**
 * Lean, single line version.
 */
@Composable
fun <FormData> LeanBoolFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    getter: (FormData) -> Boolean,
    setter: (FormData, Boolean) -> FormData,
    checkboxModifier: Modifier = Modifier,
) {
    FormField(
        id = id,
        state = state,
        getter = getter,
        setter = setter,
    ) { value, _, onValueChange ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = value,
                onCheckedChange = onValueChange,
                modifier = checkboxModifier
            )
            Text(name)
        }
    }
}