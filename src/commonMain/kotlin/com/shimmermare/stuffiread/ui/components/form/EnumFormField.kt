package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.shimmermare.stuffiread.ui.components.text.FixedOutlinedTextField
import kotlin.reflect.KClass

/**
 * @param allowedValues restrict available enum values. By default all values are selectable.
 * @param displayNameProvider to use custom enum names. By default [Enum.name] will be used.
 */
class EnumFormField<T, E : Enum<E>>(
    name: String,
    description: String? = null,
    enumType: KClass<E>,
    getter: (T) -> E,
    setter: (T, E) -> T,
    private val allowedValues: Set<E> = enumType.java.enumConstants.toSet(),
    private val displayNameProvider: (E) -> String = { it.name },
    private val textInputModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
) : FormField<T, E>(
    name,
    description,
    getter,
    setter
) {

    @Composable
    override fun renderInputField(value: FormFieldValue<E>, onValueChange: (E) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        //This value is used to assign to the DropDown the same width as TextField
        var textFieldSize by remember { mutableStateOf(Size.Zero) }


        Column {
            FixedOutlinedTextField(
                value = displayNameProvider(value.value),
                modifier = textInputModifier
                    .onGloballyPositioned { textFieldSize = it.size.toSize() }
                    .clickable { expanded = !expanded },
                trailingIcon = {
                    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                    Icon(icon, null, Modifier.clickable { expanded = !expanded })
                },
                singleLine = true,
                // Because readOnly field consumes clicks (aka no open dropdown on click) - use disabled field
                enabled = false,
                // Change disabled field to use default text color
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(
                        LocalContentAlpha.current
                    )
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                allowedValues.forEach { value ->
                    DropdownMenuItem(onClick = {
                        onValueChange(value)
                        expanded = false
                    }) {
                        Text(text = displayNameProvider(value))
                    }
                }
            }
        }
    }
}