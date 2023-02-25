package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.input.EnumOutlinedField
import kotlin.reflect.KClass

/**
 * @param allowedValues restrict available enum values. By default all values are selectable.
 * @param displayNameProvider to use custom enum names. By default [Enum.name] will be used.
 */
@Composable
fun <FormData, E : Enum<E>> EnumFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String,
    description: String? = null,
    getter: (FormData) -> E,
    setter: (FormData, E) -> FormData,
    enumType: KClass<E>,
    allowedValues: Set<E> = enumType.java.enumConstants.toSet(),
    displayNameProvider: (E) -> String = { it.name },
    inputModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
) {
    FormField(
        id = id,
        state = state,
        name = name,
        description = description,
        getter = getter,
        setter = setter,
    ) { value, _, onValueChange ->
        EnumOutlinedField(
            value = value,
            enumType = enumType,
            allowedValues = allowedValues,
            displayNameProvider = displayNameProvider,
            inputFieldModifier = inputModifier,
            onValueChange = { onValueChange(it!!) }
        )
    }
}