package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> InputForm(
    value: T,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    showResetButton: Boolean = true,
    submitButtonText: String = "Submit",
    canSubmitWithoutChanges: Boolean = false,
    onSubmit: (T) -> Unit,
    fields: List<FormField<T, out Any?>>
) {
    var loaded: Boolean by remember(value) { mutableStateOf(false) }

    var defaultValues: List<FormFieldValue<*>> by remember { mutableStateOf(emptyList()) }

    val fieldValues: MutableList<FormFieldValue<*>> = remember { mutableStateListOf() }
    val valuesAreValid: Boolean = remember(fieldValues) { fieldValues.all { it.valid } }

    var somethingChanged: Boolean by remember(value) { mutableStateOf(canSubmitWithoutChanges) }

    LaunchedEffect(value) {
        defaultValues = fields.map { it.getAndValidateValue(value) }
        fieldValues.clear()
        fieldValues.addAll(defaultValues)
        loaded = true
    }

    fun resetFieldValues() {
        fieldValues.clear()
        fieldValues.addAll(defaultValues)
    }


    if (loaded) {
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
                            somethingChanged = it.value != defaultValues[index].value
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.height(48.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (onCancel != null) {
                        Button(onClick = onCancel) {
                            Text("Cancel")
                        }
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
}