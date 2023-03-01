package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.OutlinedIntField

@Composable
fun <FormData> IntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> Int,
    setter: (FormData, Int) -> FormData,
    inputModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
    range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE),
) {
    IntFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
        inputModifier = inputModifier,
        validator = {
            if (it !in range) {
                ValidationResult(false, "Value is out of range $range")
            } else {
                ValidationResult.Valid
            }
        }
    )
}

@Composable
fun <FormData> IntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> Int,
    setter: (FormData, Int) -> FormData,
    inputModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
    validator: suspend (Int) -> ValidationResult = { ValidationResult.Valid },
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
        OutlinedIntField(
            value = value,
            modifier = inputModifier,
            isError = !valid,
            onValueChange = onValueChange
        )
    }
}