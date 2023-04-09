package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.file.StoryFileMeta

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