package com.shimmermare.stuffiread.ui.pages.openarchive

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.util.DirectoriesOnlyFileFilter
import com.shimmermare.stuffiread.util.FileDialog
import com.shimmermare.stuffiread.util.FileFilter
import com.shimmermare.stuffiread.util.SelectionMode
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArchiveDirectorySelector(onSelected: ((archiveDirectory: Path, createIfNotExists: Boolean) -> Unit)) {
    var currentErrorMessage: String? by remember { mutableStateOf(null) }

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
    }

    if (currentErrorMessage != null) {
        FixedAlertDialog(
            title = { Text("Story archive error") },
            text = { Text(currentErrorMessage ?: "") },
            modifier = Modifier.widthIn(),
            confirmButton = {
                Button(onClick = { currentErrorMessage = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { currentErrorMessage = null }
        )
    }
}

@Composable
private fun ArchiveDirSelectorButtons(
    onSelected: (file: Path, createIfNotExists: Boolean) -> Unit
) {
    SelectArchiveDirButton { onSelected.invoke(it, false) }
    CreateArchiveDirButton { onSelected.invoke(it, true) }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SelectArchiveDirButton(onSelected: (Path) -> Unit) {
    var showNotExistForDir: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val dir = FileDialog.showOpenDialog(
                title = "Select story archive directory",
                fileFilter = storyArchiveFileFilter(),
                selectionMode = SelectionMode.DIRECTORIES_ONLY,
            )
            if (dir != null) {
                if (dir.notExists()) {
                    showNotExistForDir = dir
                } else {
                    onSelected.invoke(dir)
                }
            }
        }
    ) {
        Text("Open")
    }

    if (showNotExistForDir != null) {
        FixedAlertDialog(
            title = {
                Text("Failed to select story archive directory")
            },
            text = {
                Text("Directory '$showNotExistForDir' doesn't exist.")
            },
            modifier = Modifier.widthIn(),
            confirmButton = {
                Button(onClick = { showNotExistForDir = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { showNotExistForDir = null }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CreateArchiveDirButton(onSelected: (Path) -> Unit) {
    var showAlreadyExistForDir: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val dir = FileDialog.showSaveDialog(
                title = "Create story archive directory",
                fileFilter = storyArchiveFileFilter(),
                selectionMode = SelectionMode.DIRECTORIES_ONLY,
            )
            if (dir != null) {
                if (dir.exists()) {
                    showAlreadyExistForDir = dir
                } else {
                    onSelected.invoke(dir)
                }
            }
        }
    ) {
        Text("Create")
    }

    if (showAlreadyExistForDir != null) {
        FixedAlertDialog(
            title = {
                Text("Failed to create story archive directory")
            },
            text = {
                Text("Directory '$showAlreadyExistForDir' already exists.")
            },
            modifier = Modifier.widthIn(),
            confirmButton = {
                Button(onClick = { showAlreadyExistForDir = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { showAlreadyExistForDir = null }
        )
    }
}

private fun storyArchiveFileFilter(): FileFilter {
    return DirectoriesOnlyFileFilter("Story archive directory")
}