package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.ui.util.toHumanReadableBytes

@Composable
fun StoryFileListView(files: List<StoryFileMeta>) {
    Column(
        modifier = Modifier
            .width(800.dp)
            .padding(start = 2.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        files.forEach { StoryFileCard(it) }
    }
}

@Composable
private fun StoryFileCard(meta: StoryFileMeta) {
    Box(
        modifier = Modifier.padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
    ) {
        Surface(
            modifier = Modifier.border(1.dp, Color.LightGray),
            elevation = 3.dp
        ) {
            StoryFileCardContent(
                meta = meta,
            )
        }
    }
}

@Composable
fun StoryFileCardContent(meta: StoryFileMeta, actions: @Composable (() -> Unit)? = null) {
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
        if (actions != null) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                actions()
            }
        }
    }
}