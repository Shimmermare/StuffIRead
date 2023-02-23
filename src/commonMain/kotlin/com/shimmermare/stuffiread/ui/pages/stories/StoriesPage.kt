package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.search.SearchList
import com.shimmermare.stuffiread.ui.components.story.StoryCard
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier

class StoriesPage(
    private val ignoreInvalidStories: Boolean = false
) : LoadedPage<List<Story>>() {
    override suspend fun load(app: AppState): List<Story> {
        return app.storyArchive!!.storyService.getAllStories(ignoreInvalid = ignoreInvalidStories)
    }

    @Composable
    override fun LoadingError(app: AppState) {
        Napier.e(error) { "Failed to load stories" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load stories",
                exception = error,
                actions = listOf(
                    ErrorPage.Action("Try Again") {
                        app.router.goTo(StoriesPage())
                    },
                    ErrorPage.Action("Ignore invalid stories") {
                        app.router.goTo(StoriesPage(ignoreInvalidStories = true))
                    }
                )
            )
        )
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ignoreInvalidStories) {
                        Text(
                            text = "Invalid stories ignored!\nClick to cancel",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.clickable { app.router.goTo(StoriesPage()) }
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            // TODO: goTo create story page
                        }
                    ) {
                        Icon(Icons.Filled.Add, null)
                    }
                }
            }
        ) {
            SearchList(
                items = content!!,
                nameGetter = { it.name },
                unitNameProvider = { if (it == 1) "story" else "stories" }
            ) { filtered ->
                // TODO: Add vertical scrollbar when updated to Compose 1.4
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 1000.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                ) {
                    filtered.forEach {
                        item {
                            StoryCard(app, it)
                        }
                    }
                }
            }
        }
    }
}