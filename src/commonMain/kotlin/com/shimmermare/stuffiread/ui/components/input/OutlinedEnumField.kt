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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.util.remember

/**
 * Enum selection in form of dropdown with optional selected value.
 */
@Composable
fun <E : Enum<E>> OptionalOutlinedEnumField(
    value: E? = null,
    allowedValues: Set<E>,
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
            DropdownField(
                expanded = expanded,
                value = value,
                displayNameProvider = displayNameProvider,
                inputFieldModifier = inputFieldModifier,
                onTextFieldSizeChange = { textFieldSize = it },
                onClick = { expanded = !expanded }
            )
            if (value != null) {
                IconButton(onClick = { onValueChange(null) }) {
                    Icon(Icons.Filled.Close, null)
                }
            }
        }
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

/**
 * Enum selection in form of dropdown.
 */
@Composable
fun <E : Enum<E>> OutlinedEnumField(
    value: E,
    allowedValues: Set<E>,
    displayNameProvider: (E) -> String = { it.name },
    inputFieldModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
    onValueChange: (E) -> Unit,
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
private fun <E> DropdownField(
    expanded: Boolean,
    value: E?,
    displayNameProvider: (E) -> String,
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
        placeholder = { Text(Strings.dropdownField_clickToSelect.remember()) },
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
private fun <E> DropdownMenu(
    expanded: Boolean,
    values: Set<E>,
    displayNameProvider: (E) -> String,
    textFieldSize: Size,
    onDismissRequest: () -> Unit,
    onClick: (E) -> Unit,
) {
    val valuesState by rememberUpdatedState(values)
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
    ) {
        valuesState.forEach { value ->
            DropdownMenuItem(onClick = { onClick(value) }) {
                Text(text = displayNameProvider(value))
            }
        }
    }
}