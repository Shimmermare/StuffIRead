package com.shimmermare.stuffiread.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.components.error.ErrorInfo
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.util.remember

@Composable
inline fun ErrorDialog(
    error: ErrorInfo,
    modifier: Modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp, maxHeight = 600.dp),
    dismissButtonText: String = Strings.components_dialog_error_dismissButtonDefault.remember(),
    noinline onDismissRequest: () -> Unit,
    crossinline actions: @Composable () -> Unit = {},
) {
    FullscreenPopup {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
        ) {
            ErrorCard(
                error,
                modifier = Modifier
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Button(onClick = onDismissRequest) {
                    Text(dismissButtonText)
                }
                actions()
            }
        }
    }
}