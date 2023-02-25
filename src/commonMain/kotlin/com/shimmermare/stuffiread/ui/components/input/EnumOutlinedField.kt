package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.reflect.KClass

/**
 * Enum selection with dropdown and customizable allowed values.
 *
 * [onValueChange] - if [canBeCleared] is false - value is guaranteed to not be null.
 */
@Composable
fun <E : Enum<E>> EnumOutlinedField(
    value: E? = null,
    enumType: KClass<E>,
    allowedValues: Set<E> = enumType.java.enumConstants.toSet(),
    canBeCleared: Boolean = false,
    displayNameProvider: (E) -> String = { it.name },
    inputFieldModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
    onValueChange: (E?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    //This value is used to assign to the DropDown the same width as TextField
    var textFieldSize by remember { mutableStateOf(Size.Zero) }


    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FixedOutlinedTextField(
                value = value?.let { displayNameProvider(it) } ?: "",
                modifier = inputFieldModifier
                    .onGloballyPositioned { textFieldSize = it.size.toSize() }
                    .clickable { expanded = !expanded },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                placeholder = { Text("Click to select") },
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
            if (canBeCleared && value != null) {
                IconButton(onClick = { onValueChange(null) }) {
                    Icon(Icons.Filled.Close, null)
                }
            }
        }
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