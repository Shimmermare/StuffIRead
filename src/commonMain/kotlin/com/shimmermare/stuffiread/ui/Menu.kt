package com.shimmermare.stuffiread.ui

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun Menu(
    app: AppState,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onResetAppStateRequest: () -> Unit,
    onOpenSettingsRequest: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (app.storyArchive != null) {
            DropdownMenuItem(onClick = onResetAppStateRequest) {
                Text("Close archive")
            }
        }
        DropdownMenuItem(onClick = onOpenSettingsRequest) {
            Text("Settings")
        }
        DropdownMenuItem(onClick = { uriHandler.openUri(AppState.GITHUB_URL) }) {
            Text("Open GitHub")
        }
    }
}