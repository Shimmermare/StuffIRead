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
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun <FormData, FieldValue> FormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
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
            state.invalidFields[id] = validationResult.error ?: Strings.components_form_error_invalidValue()
        }
    }

    FormFieldContent(
        value = value,
        onValueChange = { value = it },
        name = name,
        description = description,
        valid = valid,
        error = error,
        input = input
    )
}

@Composable
fun <FieldValue> FormFieldContent(
    value: FieldValue,
    onValueChange: (FieldValue) -> Unit,
    name: String? = null,
    description: String? = null,
    valid: Boolean = true,
    error: String? = null,
    input: @Composable (value: FieldValue, valid: Boolean, onValueChange: (FieldValue) -> Unit) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        FormFieldInfo(
            name = name,
            description = description,
            valid = valid,
            error = error,
        )
        input(value, valid, onValueChange)
    }
}

@Composable
fun FormFieldInfo(
    name: String? = null,
    description: String? = null,
    valid: Boolean = true,
    error: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (!name.isNullOrEmpty()) {
            Text(text = name, style = MaterialTheme.typography.h6)
        }
        if (!description.isNullOrEmpty()) {
            Text(text = description, style = MaterialTheme.typography.body1)
        }

        if (!valid) {
            Text(
                text = error ?: Strings.components_form_error_invalidValue.remember(),
                color = MaterialTheme.colors.error
            )
        }
    }
}