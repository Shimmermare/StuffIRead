package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.ui.util.FileDialog
import com.shimmermare.stuffiread.ui.util.SelectionMode
import com.shimmermare.stuffiread.ui.util.toHumanReadableBytes
import com.shimmermare.stuffiread.util.dropAt
import com.shimmermare.stuffiread.util.replaceAt
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readBytes

@Composable
fun StoryFiles(files: List<StoryFile>, onValueChange: (List<StoryFile>) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = openAndLoadStoryFile()
                        if (result != null) {
                            // Replace file if it's already added
                            val sameFileIndex = files.indexOfFirst {
                                it.meta.fileName.equals(result.meta.fileName, ignoreCase = true)
                            }
                            if (sameFileIndex >= 0) {
                                onValueChange(files.replaceAt(sameFileIndex, result))
                            } else {
                                onValueChange(files + result)
                            }
                        }
                    }
                }
            ) {
                Text("Add from file")
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .heightIn(max = 2000.dp)
                .width(800.dp)
                .padding(start = 2.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        ) {
            itemsIndexed(files) { index, file ->
                StoryFileCard(
                    meta = file.meta,
                    index = index,
                    filesTotal = files.size,
                    onDeleteRequest = {
                        onValueChange(files.dropAt(index))
                    },
                    onIndexChangeRequest = { newIndex ->
                        val result = files.toMutableList()
                        result.removeAt(index)
                        result.add(newIndex, file)
                        onValueChange(result)
                    }
                )
            }
        }
    }
}

@Composable
private fun StoryFileCard(
    meta: StoryFileMeta,
    index: Int,
    filesTotal: Int,
    onDeleteRequest: () -> Unit,
    onIndexChangeRequest: (Int) -> Unit
) {
    Box(
        modifier = Modifier.padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
    ) {
        ContextMenuArea(
            items = {
                buildList {
                    if (index > 0) {
                        add(ContextMenuItem("Move up") { onIndexChangeRequest(index - 1) })
                    }
                    if (index < filesTotal - 1) {
                        add(ContextMenuItem("Move down") { onIndexChangeRequest(index + 1) })
                    }
                    if (index > 0) {
                        add(ContextMenuItem("Move to top") { onIndexChangeRequest(0) })
                    }
                    if (index < filesTotal - 1) {
                        add(ContextMenuItem("Move to bottom") { onIndexChangeRequest(filesTotal - 1) })
                    }
                }
            }
        ) {
            Surface(
                modifier = Modifier.border(1.dp, Color.LightGray),
                elevation = 6.dp
            ) {
                StoryFileCardContent(
                    meta = meta,
                    index = index,
                    lastFile = index == filesTotal - 1,
                    onDeleteRequest = onDeleteRequest,
                    onIndexChangeRequest = onIndexChangeRequest
                )
            }
        }
    }
}

@Composable
private fun StoryFileCardContent(
    meta: StoryFileMeta,
    index: Int,
    lastFile: Boolean,
    onDeleteRequest: () -> Unit,
    onIndexChangeRequest: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(meta.originalName, style = MaterialTheme.typography.h6)
            Text("File: " + meta.fileName)
            if (meta.format != StoryFileFormat.OTHER) {
                Text("Format: ${meta.format.name} (${meta.format.extension})")
                Text("Word count: " + meta.wordCount)
            } else {
                Text("Unrecognized format", color = MaterialTheme.colors.error, fontWeight = FontWeight.Bold)
            }
            Text("Size: " + meta.size.toHumanReadableBytes())
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onDeleteRequest) {
                Icon(Icons.Filled.Clear, null)
            }
            IconButton(
                onClick = { onIndexChangeRequest(index - 1) },
                enabled = index > 0
            ) {
                Icon(Icons.Filled.ArrowUpward, null)
            }
            IconButton(
                onClick = { onIndexChangeRequest(index + 1) },
                enabled = !lastFile
            ) {
                Icon(Icons.Filled.ArrowDownward, null)
            }
        }
    }
}

private fun openAndLoadStoryFile(): StoryFile? {
    val filePath = FileDialog.showOpenDialog(
        title = "Open story file",
        selectionMode = SelectionMode.FILES_ONLY,
    ) ?: return null

    val format = StoryFileFormat.getByExtension(filePath.extension)

    val content = filePath.readBytes()

    return StoryFile(
        meta = StoryFileMeta(
            fileName = filePath.name,
            format = format,
            originalName = filePath.name,
            added = Clock.System.now(),
            wordCount = countWords(content),
            size = content.size.toUInt()
        ),
        content = content
    )
}

private fun countWords(content: ByteArray): UInt {
    // TODO: Use some smart way to count words
    // For now assume it's UTF8 string
    return StoryFile.countWords(String(content, charset = Charsets.UTF_8))
}