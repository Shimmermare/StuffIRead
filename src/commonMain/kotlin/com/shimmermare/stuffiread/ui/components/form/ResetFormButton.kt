import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.ui.components.form.InputFormState

/**
 * Form action button that will restore form data to [originalData].
 * Disabled if current form data and [originalData] are equal.
 */
@Composable
fun <FormData> ResetFormButton(
    state: InputFormState<FormData>,
    originalData: FormData,
    name: String = "Reset"
) {
    Button(
        onClick = { state.data = originalData },
        enabled = state.data != originalData
    ) {
        Text(name)
    }
}