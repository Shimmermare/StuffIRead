package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.ui.util.toHumanReadableBytes

@Composable
fun StoryFileCard(
    meta: StoryFileMeta,
    actions: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
    ) {
        Surface(
            modifier = Modifier.border(1.dp, Color.LightGray),
            elevation = 3.dp
        ) {
            StoryFileCardContent(meta, actions)
        }
    }
}

@Composable
private fun StoryFileCardContent(meta: StoryFileMeta, actions: @Composable (() -> Unit)?) {
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
            Text(Strings.components_storyFileCard_fileName.remember(meta.fileName))
            if (meta.format != StoryFileFormat.OTHER) {
                Text(Strings.components_storyFileCard_format.remember(meta.format.name, meta.format.extension))
                Text(Strings.components_storyFileCard_wordCount.remember(meta.wordCount))
            } else {
                Text(
                    Strings.components_storyFileCard_format_unrecognized.remember(),
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold
                )
            }
            val sizeStr = remember(meta.size) { meta.size.toHumanReadableBytes() }
            Text(Strings.components_storyFileCard_size.remember(sizeStr))
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