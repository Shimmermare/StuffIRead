package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.runtime.Composable

class CustomFormField<T, V>(
    name: String,
    description: String? = null,
    getter: (T) -> V,
    setter: (T, V) -> T,
    validator: (V) -> ValidationResult = { ValidationResult.Valid },
    private val inputField: @Composable (value: FormFieldValue<V>, onValueChange: (V) -> Unit) -> Unit
) : FormField<T, V>(name, description, getter, setter, validator) {
    @Composable
    override fun renderInputField(value: FormFieldValue<V>, onValueChange: (FormFieldValue<V>) -> Unit) {
        inputField(value) {
            onValueChange(FormFieldValue(it, validate(it)))
        }
    }
}