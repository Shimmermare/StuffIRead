package com.shimmermare.stuffiread.ui.components.input

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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

/**
 * Selection of one of allowed values in form of dropdown.
 */
@Composable
fun <T> OutlinedDropdownField(
    value: T,
    allowedValues: Set<T>,
    displayNameProvider: (T) -> String,
    inputFieldModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
    onValueChange: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    //This value is used to assign to the DropDown the same width as TextField
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column {
        DropdownField(
            expanded = expanded,
            value = value,
            displayNameProvider = displayNameProvider,
            inputFieldModifier = inputFieldModifier,
            onTextFieldSizeChange = { textFieldSize = it },
            onClick = { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            values = allowedValues,
            displayNameProvider = displayNameProvider,
            textFieldSize = textFieldSize,
            onDismissRequest = { expanded = false },
            onClick = { onValueChange(it); expanded = false }
        )
    }
}

@Composable
private fun <T> DropdownField(
    expanded: Boolean,
    value: T,
    displayNameProvider: (T) -> String,
    inputFieldModifier: Modifier,
    onTextFieldSizeChange: (Size) -> Unit,
    onClick: () -> Unit,
) {
    ExtendedOutlinedTextField(
        value = value?.let { displayNameProvider(it) } ?: "",
        modifier = inputFieldModifier
            .onGloballyPositioned { onTextFieldSizeChange(it.size.toSize()) }
            .clickable(onClick = onClick),
        trailingIcon = {
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
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
}

@Composable
private fun <T> DropdownMenu(
    expanded: Boolean,
    values: Set<T>,
    displayNameProvider: (T) -> String,
    textFieldSize: Size,
    onDismissRequest: () -> Unit,
    onClick: (T) -> Unit,
) {
    val valuesState by rememberUpdatedState(values)
    val displayValues = remember(valuesState) { values.map { it to displayNameProvider(it) } }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
    ) {
        displayValues.forEach { (value, name) ->
            DropdownMenuItem(onClick = { onClick(value) }) {
                Text(text = name)
            }
        }
    }
}