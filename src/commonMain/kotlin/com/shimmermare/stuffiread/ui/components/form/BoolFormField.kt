package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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