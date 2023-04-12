package com.shimmermare.stuffiread.ui

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.components.AboutApp
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun AppMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onOpenSettingsRequest: () -> Unit
) {
    var showAbout: Boolean by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (StoryArchiveHolder.isOpen) {
            DropdownMenuItem(
                onClick = {
                    StoryArchiveHolder.closeStoryArchive()
                    onDismissRequest()
                }
            ) {
                Text(Strings.appMenu_closeArchive.remember())
            }
        }
        DropdownMenuItem(
            onClick = {
                onOpenSettingsRequest()
                onDismissRequest()
            }
        ) {
            Text(Strings.appMenu_settings.remember())
        }
        DropdownMenuItem(
            onClick = {
                showAbout = true
                onDismissRequest()
            }
        ) {
            Text(Strings.appMenu_about.remember())
        }
    }

    if (showAbout) {
        AboutApp(onDismissRequest = { showAbout = false })
    }
}