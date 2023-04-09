package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun BackFormButton(
    onBack: () -> Unit,
    text: String = Strings.components_form_buttons_back.remember()
) {
    Button(onClick = onBack) {
        Text(text)
    }
}

/**
 * Form action button that will restore form data to [originalData].
 * Disabled if current form data and [originalData] are equal.
 */
@Composable
fun <FormData> ResetFormButton(
    state: InputFormState<FormData>,
    originalData: FormData,
    text: String = Strings.components_form_buttons_reset.remember()
) {
    Button(
        onClick = { state.data = originalData },
        enabled = state.data != originalData || !state.isValid
    ) {
        Text(text)
    }
}