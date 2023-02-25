package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun <FormData> SubmittableInputForm(
    data: FormData,
    modifier: Modifier = Modifier,
    submitButtonText: String = "Submit",
    canSubmitWithoutChanges: Boolean = false,
    onSubmit: (FormData) -> Unit,
    actions: (@Composable (InputFormState<FormData>) -> Unit)? = null,
    fields: @Composable (state: InputFormState<FormData>) -> Unit,
) {
    val state: InputFormState<FormData> = remember(data) { InputFormState(data) }
    val somethingChanged: Boolean = remember(state, state.data) { data != state.data }

    InputForm(
        state = state,
        modifier = modifier,
        actions = {
            if (actions != null) actions(state)
            Button(
                onClick = {
                    onSubmit(state.data)
                },
                enabled = (canSubmitWithoutChanges || somethingChanged) && state.isValid,
            ) {
                Text(submitButtonText)
            }
        },
        fields = fields
    )
}