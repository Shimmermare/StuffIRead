package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun <FormData, FieldValue> OptionalFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    defaultValue: () -> FieldValue,
    getter: (FormData) -> FieldValue?,
    setter: (FormData, FieldValue?) -> FormData,
    validator: suspend (FieldValue?) -> ValidationResult = { ValidationResult.Valid },
    input: @Composable (value: FieldValue, valid: Boolean, onValueChange: (FieldValue) -> Unit) -> Unit
) {
    FormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
        validator = validator
    ) { value, valid, onValueChange ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (value == null) {
                Text("Not specified")
                Button(
                    onClick = { onValueChange(defaultValue()) },
                ) {
                    Text("Set")
                }
            } else {
                input(value, valid, onValueChange)

                IconButton(
                    onClick = { onValueChange(null) },
                ) {
                    Icon(Icons.Filled.Clear, null)
                }
            }
        }
    }
}