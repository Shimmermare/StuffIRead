package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.text.FixedOutlinedTextField

class TextFormField<T>(
    name: String,
    description: String? = null,
    getter: (T) -> String,
    setter: (T, String) -> T,
    validator: (String) -> ValidationResult = { ValidationResult.Valid },
    private val textInputModifier: Modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 36.dp, maxHeight = 420.dp),
    private val singleLine: Boolean = true
) : FormField<T, String>(name, description, getter, setter, validator) {
    @Composable
    override fun renderInputField(value: FormFieldValue<String>, onValueChange: (FormFieldValue<String>) -> Unit) {
        FixedOutlinedTextField(
            value = value.value,
            modifier = textInputModifier,
            isError = !value.valid,
            singleLine = singleLine,
            onValueChange = {
                onValueChange(FormFieldValue(it, validate(it)))
            }
        )
    }
}