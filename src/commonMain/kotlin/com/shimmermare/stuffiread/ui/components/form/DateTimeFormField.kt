package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.ui.components.input.DateTimePicker
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun <FormData> DateTimeFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    getter: (FormData) -> LocalDateTime,
    setter: (FormData, LocalDateTime) -> FormData,
    validator: suspend (LocalDateTime) -> ValidationResult = { ValidationResult.Valid },
) {
    FormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
        validator = validator,
    ) { value, _, onValueChange ->
        DateTimePicker(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun <FormData> OptionalDateTimeFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    defaultValue: () -> LocalDateTime = { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) },
    getter: (FormData) -> LocalDateTime?,
    setter: (FormData, LocalDateTime?) -> FormData,
    validator: suspend (LocalDateTime?) -> ValidationResult = { ValidationResult.Valid },
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
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun <FormData> OptionalInstantFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
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
            onValueChange = { onValueChange(it.toInstant(timeZone)) }
        )
    }
}
