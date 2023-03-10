package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.ui.components.input.datetime.DateTimePicker
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun <FormData> OptionalInstantFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    showSeconds: Boolean = false,
    defaultValue: () -> Instant = { Clock.System.now() },
    getter: (FormData) -> Instant?,
    setter: (FormData, Instant?) -> FormData,
    validator: suspend (Instant?) -> ValidationResult = { ValidationResult.Valid },
) {
    OptionalFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        defaultValue = defaultValue,
        getter = getter,
        setter = setter,
        validator = validator,
    ) { value, _, onValueChange ->
        DateTimePicker(
            value = value.toLocalDateTime(timeZone),
            showSeconds = showSeconds,
            onValueChange = { onValueChange(it.toInstant(timeZone)) }
        )
    }
}

@Composable
fun <FormData> OptionalInstantRangeFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    showSeconds: Boolean = false,
    defaultValue: () -> Instant,
    fromGetter: (FormData) -> Instant?,
    toGetter: (FormData) -> Instant?,
    setter: (FormData, lower: Instant?, upper: Instant?) -> FormData,
    validator: suspend (from: Instant?, to: Instant?) -> ValidationResult = { _, _ -> ValidationResult.Valid },
) {
    OptionalRangeFormField(
        id = id,
        state = state,
        name = name,
        description = description,
        defaultValue = defaultValue,
        fromGetter = fromGetter,
        toGetter = toGetter,
        setter = setter,
        validator = validator,
    ) { value, _, onValueChange ->
        DateTimePicker(
            value = value.toLocalDateTime(timeZone),
            showSeconds = showSeconds,
            onValueChange = { onValueChange(it.toInstant(timeZone)) }
        )
    }
}