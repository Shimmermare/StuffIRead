package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.text.FixedOutlinedTextField

class IntFormField<T>(
    name: String,
    description: String? = null,
    getter: (T) -> Int,
    setter: (T, Int) -> T,
    range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE),
    validator: (Int) -> ValidationResult = { ValidationResult.Valid },
    private val textInputModifier: Modifier = Modifier.fillMaxWidth().height(36.dp),
) : FormField<T, Int>(
    name,
    description,
    getter,
    setter,
    validator = {
        if (it !in range) {
            ValidationResult(false, "Value is out of range $range")
        } else {
            validator(it)
        }
    }
) {

    @Composable
    override fun renderInputField(value: FormFieldValue<Int>, onValueChange: (FormFieldValue<Int>) -> Unit) {
        FixedOutlinedTextField(
            value = value.value.toString(),
            modifier = textInputModifier,
            isError = !value.valid,
            singleLine = true,
            onValueChange = {
                val negative = it.startsWith('-')
                val onlyDigits = it.filter { c -> c.isDigit() }.let { s ->
                    when {
                        s.isEmpty() -> "0"
                        negative -> "-$s"
                        else -> s
                    }
                }
                val intValue = onlyDigits.toBigInteger()
                    .max(Int.MIN_VALUE.toBigInteger())
                    .min(Int.MAX_VALUE.toBigInteger())
                    .intValueExact()
                onValueChange(FormFieldValue(intValue, validate(intValue)))
            }
        )
    }
}