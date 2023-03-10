package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.ui.components.layout.PopupContent

@Composable
fun <T> DropdownPicker(
    value: T,
    onValueChange: (T) -> Unit,
    displayText: (T) -> String,
    pickerField: @Composable (expanded: Boolean) -> Unit = { expanded ->
        TextDropdownPickerField(value, displayText, expanded)
    },
    dropdownValues: List<T>,
    dropdownItem: @Composable (item: T) -> Unit = { item ->
        TextDropdownItem(item, picked = item == value, displayText)
    },
    dropdownContentModifier: Modifier = Modifier.width(IntrinsicSize.Max)
) {
    DropdownPicker(
        value = value,
        onValueChange = onValueChange,
        displayText = displayText,
        pickerField = pickerField
    ) { _, innerOnValueChange ->
        Column(
            modifier = dropdownContentModifier.verticalScroll(rememberScrollState())
        ) {
            dropdownValues.forEach { item ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .let {
                            if (item == value) it.background(MaterialTheme.colors.primary) else it
                        }
                        .clickable { innerOnValueChange(item) }
                ) {
                    dropdownItem(item)
                }
            }
        }
    }
}

@Composable
fun <T> DropdownPicker(
    value: T,
    onValueChange: (T) -> Unit,
    displayText: (T) -> String,
    pickerField: @Composable (expanded: Boolean) -> Unit = { expanded ->
        TextDropdownPickerField(value, displayText, expanded)
    },
    dropdownContent: @Composable (value: T, onPick: (T) -> Unit) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

    Box {
        Box(modifier = Modifier.clickable { expanded = !expanded }) {
            pickerField(expanded)
        }
        if (expanded) {
            Popup(
                focusable = true,
                onDismissRequest = { expanded = false }
            ) {
                PopupContent(
                    border = false
                ) {
                    dropdownContent(value) {
                        expanded = false
                        onValueChange(it)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> TextDropdownPickerField(value: T, displayText: (T) -> String, expanded: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Text(displayText(value))
        Icon(if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, null)
    }
}

@Composable
fun <T> TextDropdownItem(value: T, picked: Boolean, displayText: (T) -> String) {
    Text(
        text = displayText(value),
        color = if (picked) MaterialTheme.colors.onPrimary else Color.Unspecified,
        modifier = Modifier.padding(5.dp).fillMaxWidth()
    )
}