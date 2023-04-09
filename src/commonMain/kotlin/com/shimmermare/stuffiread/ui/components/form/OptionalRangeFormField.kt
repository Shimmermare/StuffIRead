package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.util.remember

/**
 * Inclusive on both ends.
 */
@Composable
fun <FormData, FieldValue : Comparable<FieldValue>> OptionalRangeFormField(
    id: String,
    state: InputFormState<FormData>,
    name: String? = null,
    description: String? = null,
    defaultValue: () -> FieldValue,
    fromGetter: (FormData) -> FieldValue?,
    toGetter: (FormData) -> FieldValue?,
    setter: (FormData, lower: FieldValue?, upper: FieldValue?) -> FormData,
    validator: suspend (from: FieldValue?, to: FieldValue?) -> ValidationResult = { _, _ -> ValidationResult.Valid },
    input: @Composable (value: FieldValue, valid: Boolean, onValueChange: (FieldValue) -> Unit) -> Unit
) {
    var from: FieldValue? by remember(fromGetter(state.data)) { mutableStateOf(fromGetter(state.data)) }
    var to: FieldValue? by remember(toGetter(state.data)) { mutableStateOf(toGetter(state.data)) }

    var valid: Boolean by remember { mutableStateOf(false) }
    var error: String? by remember { mutableStateOf(null) }

    val invalidRangeText = Strings.components_form_rangeField_invalidRange.remember()
    LaunchedEffect(from, to, invalidRangeText) {
        val validationResult = if (from != null && to != null && from!! > to!!) {
            ValidationResult(false, invalidRangeText)
        } else {
            validator(from, to)
        }

        valid = validationResult.valid
        error = validationResult.error

        if (validationResult.valid) {
            state.data = setter(state.data, from, to)
            state.invalidFields.remove(id)
        } else {
            state.invalidFields[id] = validationResult.error ?: invalidRangeText
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        FormFieldInfo(
            name = name,
            description = description,
            valid = valid,
            error = error,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(Strings.components_form_rangeField_from.remember())
            InputOrNorSpecified(from, valid, defaultValue = { to ?: defaultValue() }, input) { from = it }
            Text(Strings.components_form_rangeField_to.remember())
            InputOrNorSpecified(to, valid, defaultValue = { from ?: defaultValue() }, input) { to = it }
        }
    }
}

@Composable
private fun <FieldValue> RowScope.InputOrNorSpecified(
    value: FieldValue?,
    valid: Boolean,
    defaultValue: () -> FieldValue,
    input: @Composable (value: FieldValue, valid: Boolean, onValueChange: (FieldValue) -> Unit) -> Unit,
    onValueChange: (FieldValue?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1F, false),
    ) {
        if (value != null) {
            input(value, valid, onValueChange)
            IconButton(
                onClick = { onValueChange(null) },
            ) {
                Icon(Icons.Filled.Clear, null)
            }
        } else {
            Button(
                onClick = { onValueChange(defaultValue()) },
            ) {
                Text(Strings.components_form_rangeField_set.remember())
            }
        }
    }
}