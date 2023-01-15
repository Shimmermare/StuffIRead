package com.shimmermare.stuffiread.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shimmermare.stuffiread.StoryDatabase
import com.shimmermare.stuffiread.ui.util.showOpenDialog
import com.shimmermare.stuffiread.ui.util.showSaveDialog
import java.nio.file.Path
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun StoryDatabaseProblemsOrContent(
    storyDatabase: StoryDatabase,
    content: @Composable () -> Unit
) {
    when {
        storyDatabase.status.isError -> {
            StoryDatabaseError(
                dbStatus = storyDatabase.status,
                currentDbFile = storyDatabase.currentFile,
                onDbFileSelected = { file, create -> storyDatabase.open(file, create) }
            )
        }

        storyDatabase.status == StoryDatabase.Status.LOADING -> {
            StoryDatabaseLoading()
        }

        storyDatabase.status != StoryDatabase.Status.OK -> {
            throw IllegalStateException("Invalid db status: ${storyDatabase.status}")
        }

        else -> content.invoke()
    }
}

@Composable
fun StoryDatabaseError(
    dbStatus: StoryDatabase.Status,
    currentDbFile: Path?,
    onDbFileSelected: ((file: Path, create: Boolean) -> Unit)
) {
    val title = when (dbStatus) {
        StoryDatabase.Status.DB_FILE_NOT_SET -> "No active story database"
        else -> "Problem with story database"
    }
    val message = when (dbStatus) {
        StoryDatabase.Status.DB_FILE_NOT_SET -> "Create new or open existing story database file."
        StoryDatabase.Status.DB_FILE_MISSING -> "Story database file '$currentDbFile' does not exist.\nCreate new or open existing file."
        else -> "Unknown error with story database file '$currentDbFile': '$dbStatus'"
    }
    SelectStoryDbFile(
        title = title,
        message = message,
        onSelected = { file, create -> if (file != null) onDbFileSelected.invoke(file, create) }
    )
}

@Composable
fun StoryDatabaseLoading() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading story database...",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun SelectStoryDbFile(title: String, message: String, onSelected: ((file: Path?, create: Boolean) -> Unit)) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Text(
            text = message,
            fontWeight = FontWeight.Bold,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                onClick = {
                    onSelected.invoke(
                        showOpenDialog(
                            title = "Select story database file",
                            fileFilter = storyDatabaseFileFilter()
                        ), false
                    )
                }
            ) {
                Text("Open")
            }
            Button(
                onClick = {
                    onSelected.invoke(
                        showSaveDialog(
                            title = "Select story database file",
                            fileFilter = storyDatabaseFileFilter()
                        ), true
                    )
                }
            ) {
                Text("Create")
            }
        }
    }
}

private fun storyDatabaseFileFilter(): FileFilter {
    return FileNameExtensionFilter("Story database file (*.${StoryDatabase.STORY_DB_FILE_EXT})", StoryDatabase.STORY_DB_FILE_EXT)
}