package com.shimmermare.stuffiread.ui.components.form

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * @param actions button actions on form. E.g. reset fields to default.
 */
@Composable
fun <FormData> InputForm(
    state: InputFormState<FormData>,
    modifier: Modifier = Modifier,
    actions: (@Composable (InputFormState<FormData>) -> Unit)? = null,
    fields: @Composable (state: InputFormState<FormData>) -> Unit,
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = modifier.verticalScroll(scrollState).padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            fields(state)

            if (actions != null) {
                Row(
                    modifier = Modifier.height(48.dp), horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    actions(state)
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}

class InputFormState<FormData>(
    data: FormData
) {
    var data: FormData by mutableStateOf(data)

    val invalidFields: MutableMap<String, String> = mutableStateMapOf()
    val isValid: Boolean by derivedStateOf { invalidFields.isEmpty() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InputFormState<*>

        if (data != other.data) return false
        if (invalidFields != other.invalidFields) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.hashCode() ?: 0
        result = 31 * result + invalidFields.hashCode()
        return result
    }
}