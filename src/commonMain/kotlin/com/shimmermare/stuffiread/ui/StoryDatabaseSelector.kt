package com.shimmermare.stuffiread.ui

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
import com.shimmermare.stuffiread.ui.AppState.Companion.STORY_DB_FILE_EXT
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.ui.util.showOpenDialog
import com.shimmermare.stuffiread.ui.util.showSaveDialog
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoryDatabaseSelector(onSelected: ((AppState) -> Unit)) {
    var currentErrorMessage: String? by remember { mutableStateOf(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No active story database",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Text(
            text = "Create new or open existing story database file.",
            fontWeight = FontWeight.Bold,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DbFileSelectorButtons(
                onSelected = { file, createIfNotExists ->
                    openDbFile(
                        file = file,
                        createIfNotExists = createIfNotExists,
                        onError = { msg -> currentErrorMessage = msg },
                        onSuccess = onSelected
                    )
                }
            )
        }
    }

    if (currentErrorMessage != null) {
        FixedAlertDialog(
            title = { Text("Story database error") },
            text = { Text(currentErrorMessage!!) },
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
private fun DbFileSelectorButtons(
    onSelected: (file: Path, createIfNotExists: Boolean) -> Unit
) {
    SelectExistingStoryDbFileButton { onSelected.invoke(it, false) }
    CreateNewStoryDbFileButton { onSelected.invoke(it, true) }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SelectExistingStoryDbFileButton(onSelected: (Path) -> Unit) {
    var showNotExistForFile: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val file = showOpenDialog(
                title = "Select story database file",
                fileFilter = storyDbFileFilter()
            )
            if (file != null) {
                if (!Files.exists(file)) {
                    showNotExistForFile = file
                } else {
                    onSelected.invoke(file)
                }
            }
        }
    ) {
        Text("Open")
    }

    if (showNotExistForFile != null) {
        FixedAlertDialog(
            title = {
                Text("Failed to open story database")
            },
            text = {
                Text("Database file '$showNotExistForFile' doesn't exist.")
            },
            modifier = Modifier.widthIn(),
            confirmButton = {
                Button(onClick = { showNotExistForFile = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { showNotExistForFile = null }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CreateNewStoryDbFileButton(onSelected: (Path) -> Unit) {
    var showAlreadyExistForFile: Path? by remember { mutableStateOf(null) }

    Button(
        onClick = {
            val file = showSaveDialog(
                title = "Save story database file",
                fileFilter = storyDbFileFilter(),
                extension = STORY_DB_FILE_EXT,
            )
            if (file != null) {
                if (Files.exists(file)) {
                    showAlreadyExistForFile = file
                } else {
                    onSelected.invoke(file)
                }
            }
        }
    ) {
        Text("Create")
    }

    if (showAlreadyExistForFile != null) {
        FixedAlertDialog(
            title = {
                Text("Failed to create story database")
            },
            text = {
                Text("Database file '$showAlreadyExistForFile' already exists.")
            },
            modifier = Modifier.widthIn(),
            confirmButton = {
                Button(onClick = { showAlreadyExistForFile = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { showAlreadyExistForFile = null }
        )
    }
}

private fun storyDbFileFilter(): FileFilter {
    return FileNameExtensionFilter("Story database file (*.$STORY_DB_FILE_EXT)", STORY_DB_FILE_EXT)
}

private fun openDbFile(
    file: Path,
    createIfNotExists: Boolean,
    onError: (message: String) -> Unit,
    onSuccess: (state: AppState) -> Unit
) {
    try {
        val appState = AppState(file, createIfNotExists)
        onSuccess.invoke(appState)
    } catch (e: Exception) {
        val message = "Failed to load story database at '$file'" + (if (e.message != null) ": ${e.message}" else ": $e")
        onError.invoke(message)
    }
}