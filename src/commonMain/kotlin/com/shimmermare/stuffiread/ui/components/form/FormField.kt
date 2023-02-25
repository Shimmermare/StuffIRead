package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <FormData, FieldValue> FormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> FieldValue,
    setter: (FormData, FieldValue) -> FormData,
    validator: suspend (FieldValue) -> ValidationResult = { ValidationResult.Valid },
    input: @Composable (value: FieldValue, valid: Boolean, onValueChange: (FieldValue) -> Unit) -> Unit
) {
    var value: FieldValue by remember(getter(state.data)) { mutableStateOf(getter(state.data)) }
    var valid: Boolean by remember { mutableStateOf(false) }
    var error: String? by remember { mutableStateOf(null) }

    LaunchedEffect(value) {
        val validationResult = validator(value)
        valid = validationResult.valid
        error = validationResult.error

        if (validationResult.valid) {
            state.data = setter(state.data, value)
            state.invalidFields.remove(id)
        } else {
            state.invalidFields[id] = validationResult.error ?: "Invalid value"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(text = name, style = MaterialTheme.typography.h6)
        if (description != null) {
            Text(text = description, style = MaterialTheme.typography.body1)
        }

        if (!valid) {
            Text(
                text = error ?: "Invalid value",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.error
            )
        }
        input(value, valid) { value = it }
    }
}