package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.FixedOutlinedTextField

@Composable
fun <FormData> IntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> Int,
    setter: (FormData, Int) -> FormData,
    inputModifier: Modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 36.dp, maxHeight = 420.dp),
    range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE),
) {
    IntFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
        textInputModifier = inputModifier,
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
    textInputModifier: Modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 36.dp, maxHeight = 420.dp),
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
        FixedOutlinedTextField(
            value = value.toString(),
            modifier = textInputModifier,
            isError = !valid,
            singleLine = true,
            onValueChange = {
                val negative = it.startsWith('-')
                val onlyDigits = it.filter { c -> c.isDigit() }.let { s ->
                    when {
                        s.isEmpty() -> "0"
                        negative -> "-$s"
                        else -> s
                    }
                }
                val intValue = onlyDigits.toBigInteger()
                    .max(Int.MIN_VALUE.toBigInteger())
                    .min(Int.MAX_VALUE.toBigInteger())
                    .intValueExact()
                if (value != intValue) onValueChange(intValue)
            }
        )
    }
}