package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.StoryDatabase
import java.nio.file.Path


@Composable
@Preview
fun App(openedWithFile: Path? = null) {
    val storyDatabase = remember { StoryDatabase(openedWithFile) }

    MaterialTheme {
        TopAppBar(
            title = { Text("Stuff I Read") }
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            StoryDatabaseProblemsOrContent(storyDatabase) {
                Text("Navigation here")
            }
        }
    }
}
