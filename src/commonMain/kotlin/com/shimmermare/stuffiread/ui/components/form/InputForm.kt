package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> InputForm(
    value: T,
    modifier: Modifier = Modifier,
    showResetButton: Boolean = true,
    submitButtonText: String = "Submit",
    canSubmitWithoutChanges: Boolean = false,
    onCancel: () -> Unit,
    onSubmit: (T) -> Unit,
    fields: List<FormField<T, out Any?>>
) {
    var somethingChanged by remember(value) { mutableStateOf(canSubmitWithoutChanges) }

    // fieldValues[index] is value of fields[index]
    val fieldValues: MutableList<FormFieldValue<*>> = remember(value) {
        fields.map { it.getAndValidateValue(value) }.toMutableStateList()
    }
    val valuesAreValid: Boolean by derivedStateOf { fieldValues.all { it.valid } }

    fun resetFieldValues() {
        fieldValues.clear()
        fields.map { fieldValues.add(it.getAndValidateValue(value)) }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        userScrollEnabled = true
    ) {
        fields.forEachIndexed { index, field ->
            item {
                field.renderRaw(
                    value = fieldValues[index],
                    onValueChange = {
                        fieldValues[index] = it
                        somethingChanged = true
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.height(48.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                if (showResetButton) {
                    Button(onClick = ::resetFieldValues) {
                        Text("Reset")
                    }
                }
                Button(
                    onClick = {
                        var result = value
                        fields.forEachIndexed { index, field ->
                            // Will fail if any value is invalid
                            result = field.setValidValueRaw(result, fieldValues[index])
                        }
                        somethingChanged = false
                        onSubmit(result)
                    },
                    enabled = somethingChanged && valuesAreValid,
                ) {
                    Text(submitButtonText)
                }
            }
        }
    }
}