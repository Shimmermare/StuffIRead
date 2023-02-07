package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

abstract class FormField<T, V>(
    val name: String,
    val description: String? = null,
    val getter: (T) -> V,
    val setter: (T, V) -> T,
    val validator: (V) -> ValidationResult = { ValidationResult.Valid }
) {
    fun getAndValidateValue(formData: T): FormFieldValue<V> {
        val value = getter(formData)
        val validation = validator(value)
        return FormFieldValue(value, validation.valid, validation.error)
    }

    @Suppress("UNCHECKED_CAST")
    fun setValidValueRaw(formData: T, value: FormFieldValue<*>): T {
        return setValidValue(formData, value as FormFieldValue<V>)
    }

    fun setValidValue(formData: T, value: FormFieldValue<V>): T {
        if (!value.valid) {
            throw IllegalArgumentException("Value of field '$name' is invalid: ${value.error}")
        }
        return setter(formData, value.value)
    }

    fun validate(value: V): ValidationResult = validator(value)

    @Suppress("UNCHECKED_CAST")
    @Composable
    fun renderRaw(value: FormFieldValue<*>, onValueChange: (FormFieldValue<*>) -> Unit) {
        render(value as FormFieldValue<V>, onValueChange)
    }

    @Composable
    fun render(value: FormFieldValue<V>, onValueChange: (FormFieldValue<V>) -> Unit) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = name, style = MaterialTheme.typography.h6
            )
            if (description != null) {
                Text(
                    text = description, style = MaterialTheme.typography.body1
                )
            }
            if (!value.valid && value.error != null) {
                Text(
                    text = value.error,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.error
                )
            }
            renderInputField(value, onValueChange)
        }
    }

    @Composable
    protected abstract fun renderInputField(value: FormFieldValue<V>, onValueChange: (FormFieldValue<V>) -> Unit)
}

data class FormFieldValue<V>(
    val value: V,
    val valid: Boolean = true,
    val error: String? = null,
) {
    constructor(value: V, validation: ValidationResult): this(value, validation.valid, validation.error)
}