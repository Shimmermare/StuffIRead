package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.ExtendedOutlinedTextField

@Composable
fun <FormData> TextFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> String,
    setter: (FormData, String) -> FormData,
    textInputModifier: Modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 36.dp, maxHeight = 420.dp),
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    validator: suspend (String) -> ValidationResult = { ValidationResult.Valid },
) {
    FormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
        validator = validator,
    ) { value, valid, onValueChange ->
        ExtendedOutlinedTextField(
            value = value,
            modifier = textInputModifier,
            isError = !valid,
            singleLine = singleLine,
            maxLength = maxLength,
            onValueChange = onValueChange,
        )
    }
}