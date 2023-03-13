package com.shimmermare.stuffiread.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup

@Composable
inline fun ConfirmationDialog(
    crossinline title: @Composable () -> Unit,
    modifier: Modifier = Modifier.padding(20.dp).width(500.dp),
    dismissButtonText: String = "Cancel",
    noinline onDismissRequest: () -> Unit,
    confirmButtonText: String = "Confirm",
    confirmButtonEnabled: Boolean = true,
    noinline onConfirmRequest: () -> Unit,
    crossinline content: @Composable () -> Unit,
) {
    FullscreenPopup {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h6) {
                title()
            }

            content()

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Button(onClick = onDismissRequest) {
                    Text(dismissButtonText)
                }
                Button(enabled = confirmButtonEnabled, onClick = onConfirmRequest) {
                    Text(confirmButtonText)
                }
            }
        }
    }
}