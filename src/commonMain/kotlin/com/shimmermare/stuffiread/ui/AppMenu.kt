package com.shimmermare.stuffiread.ui

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.ui.components.AboutApp

@Composable
fun AppMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onResetAppStateRequest: () -> Unit,
    onOpenSettingsRequest: () -> Unit
) {
    var showAbout: Boolean by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (StoryArchiveHolder.isOpen) {
            DropdownMenuItem(onClick = onResetAppStateRequest) {
                Text("Close archive")
            }
        }
        DropdownMenuItem(onClick = onOpenSettingsRequest) {
            Text("Settings")
        }
        DropdownMenuItem(onClick = { showAbout = true }) {
            Text("About")
        }
    }

    if (showAbout) {
        AboutApp(onDismissRequest = { showAbout = false })
    }
}