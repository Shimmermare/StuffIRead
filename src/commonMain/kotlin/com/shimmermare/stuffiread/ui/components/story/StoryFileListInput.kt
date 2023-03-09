package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.ui.util.FileDialog
import com.shimmermare.stuffiread.ui.util.SelectionMode
import com.shimmermare.stuffiread.util.dropAt
import com.shimmermare.stuffiread.util.replaceAt
import kotlinx.coroutines.launch
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readBytes

@Composable
fun StoryFileListInput(files: List<StoryFile>, onValueChange: (List<StoryFile>) -> Unit) {
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
        LazyColumn(
            modifier = Modifier
                .heightIn(max = 10000.dp)
                .width(800.dp)
                .padding(start = 2.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            userScrollEnabled = false,
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
                elevation = 3.dp
            ) {
                StoryFileCardContent(meta) {
                    StoryFileCardActionButton(icon = Icons.Filled.Clear, onClick = onDeleteRequest)
                    StoryFileCardActionButton(Icons.Filled.ArrowUpward, enabled = index > 0) {
                        onIndexChangeRequest(index - 1)
                    }
                    StoryFileCardActionButton(Icons.Filled.ArrowDownward, enabled = index == filesTotal - 1) {
                        onIndexChangeRequest(index + 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryFileCardActionButton(icon: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .let {
                if (enabled) {
                    it.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 16.dp),
                        onClick = onClick,
                    )
                } else {
                    it
                }
            },
        tint = LocalContentColor.current.copy(
            alpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        )
    )
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