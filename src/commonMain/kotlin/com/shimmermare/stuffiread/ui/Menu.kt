package com.shimmermare.stuffiread.ui

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun Menu(expanded: Boolean, onDismissRequest: () -> Unit, onResetAppStateRequest: () -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(onClick = onResetAppStateRequest) {
            Text("Close database")
        }
        DropdownMenuItem(onClick = { /* TODO settings */ }) {
            Text("Settings")
        }
    }
}