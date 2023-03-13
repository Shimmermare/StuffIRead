package com.shimmermare.stuffiread.ui.pages.openarchive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shimmermare.stuffiread.ui.AppSettingsHolder
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.components.layout.PointerInsideTrackerBox
import com.shimmermare.stuffiread.ui.util.DirectoriesOnlyFileFilter
import com.shimmermare.stuffiread.ui.util.FileDialog
import com.shimmermare.stuffiread.ui.util.FileFilter
import com.shimmermare.stuffiread.ui.util.SelectionMode
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.notExists


@Composable
fun ArchiveDirectorySelector(onSelected: (Path) -> Unit) {
    val recentlyOpened by rememberUpdatedState(AppSettingsHolder.settings.recentlyOpenedArchives)

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No active story archive",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Text(
            text = "Create new or open existing story archive.",
            fontWeight = FontWeight.Bold,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ArchiveDirSelectorButtons(onSelected)
        }

        if (recentlyOpened.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Recent", style = MaterialTheme.typography.h6)

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy((-10).dp)
            ) {
                recentlyOpened.forEach { archiveDirectory ->
                    RecentlyOpenedItem(archiveDirectory) { onSelected(archiveDirectory) }
                }
            }
        }
    }
}

@Composable
private fun ArchiveDirSelectorButtons(
    onSelected: (file: Path) -> Unit
) {
    OpenArchiveDirButton { onSelected.invoke(it) }
    CreateArchiveDirButton { onSelected.invoke(it) }
}

@Composable
private fun OpenArchiveDirButton(onClick: (Path) -> Unit) {
    var askCreateFor: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val dir = FileDialog.showOpenDialog(
                title = "Select story archive directory",
                fileFilter = storyArchiveFileFilter(),
                selectionMode = SelectionMode.DIRECTORIES_ONLY,
            )
            if (dir != null) {
                if (dir.notExists()) {
                    askCreateFor = dir
                } else {
                    onClick.invoke(dir)
                }
            }
        }
    ) {
        Text("Open")
    }

    if (askCreateFor != null) {
        ConfirmationDialog(
            title = { Text("Directory doesn't exist") },
            onDismissRequest = {
                askCreateFor = null
            },
            confirmButtonText = "Create",
            onConfirmRequest = {
                onClick(askCreateFor!!)
                askCreateFor = null
            }
        ) {
            Text("Directory '$askCreateFor' doesn't exist. Create new story archive?")
        }
    }
}

@Composable
private fun CreateArchiveDirButton(onClick: (Path) -> Unit) {
    var askOpenFor: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val dir = FileDialog.showSaveDialog(
                title = "Create story archive directory",
                fileFilter = storyArchiveFileFilter(),
                selectionMode = SelectionMode.DIRECTORIES_ONLY,
            )
            if (dir != null) {
                if (dir.exists()) {
                    askOpenFor = dir
                } else {
                    onClick(dir)
                }
            }
        }
    ) {
        Text("Create")
    }

    if (askOpenFor != null) {
        ConfirmationDialog(
            title = { Text("Directory already exists") },
            onDismissRequest = {
                askOpenFor = null
            },
            confirmButtonText = "Open",
            onConfirmRequest = {
                onClick(askOpenFor!!)
                askOpenFor = null
            }
        ) {
            Text("Directory '$askOpenFor' already exists. Open as story archive?")
        }
    }
}

private fun storyArchiveFileFilter(): FileFilter {
    return DirectoriesOnlyFileFilter("Story archive directory")
}

@Composable
private fun RecentlyOpenedItem(archiveDirectory: Path, onClick: () -> Unit) {
    PointerInsideTrackerBox { pointerInside ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = if (pointerInside) 0.dp else 48.dp)
        ) {
            TextButton(onClick = onClick) {
                Text(archiveDirectory.absolutePathString())
            }
            if (pointerInside) {
                IconButton(
                    onClick = {
                        AppSettingsHolder.settings.let {
                            it.copy(recentlyOpenedArchives = it.recentlyOpenedArchives.minusElement(archiveDirectory))
                        }.also { AppSettingsHolder.update(it) }
                    },
                    modifier = Modifier.width(48.dp)
                ) {
                    Icon(Icons.Filled.Clear, null)
                }
            }
        }
    }
}