package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.OutlinedIntField
import com.shimmermare.stuffiread.ui.components.input.OutlinedUIntField

@Composable
fun <FormData> RangedIntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> Int,
    setter: (FormData, Int) -> FormData,
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
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
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
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

@Composable
fun <FormData> RangedOptionalIntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    defaultValue: Int = 0,
    getter: (FormData) -> Int?,
    setter: (FormData, Int?) -> FormData,
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
) {
    OptionalIntFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        defaultValue = defaultValue,
        getter = getter,
        setter = setter,
        inputModifier = inputModifier,
        validator = {
            if (it != null && it !in range) {
                ValidationResult(false, "Value is out of range $range")
            } else {
                ValidationResult.Valid
            }
        }
    )
}

@Composable
fun <FormData> OptionalIntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    defaultValue: Int = 0,
    getter: (FormData) -> Int?,
    setter: (FormData, Int?) -> FormData,
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
    validator: suspend (Int?) -> ValidationResult = { ValidationResult.Valid },
) {
    OptionalFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        defaultValue = { defaultValue },
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

@Composable
fun <FormData> OptionalUIntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    defaultValue: UInt = 0u,
    getter: (FormData) -> UInt?,
    setter: (FormData, UInt?) -> FormData,
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
    validator: suspend (UInt?) -> ValidationResult = { ValidationResult.Valid },
) {
    OptionalFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        defaultValue = { defaultValue },
        getter = getter,
        setter = setter,
        validator = validator,
    ) { value, valid, onValueChange ->
        OutlinedUIntField(
            value = value,
            modifier = inputModifier,
            isError = !valid,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun <FormData> UIntFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> UInt,
    setter: (FormData, UInt) -> FormData,
    inputModifier: Modifier = Modifier.widthIn(max = 200.dp).height(36.dp),
    validator: suspend (UInt) -> ValidationResult = { ValidationResult.Valid },
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
        OutlinedUIntField(
            value = value,
            modifier = inputModifier,
            isError = !valid,
            onValueChange = onValueChange
        )
    }
}