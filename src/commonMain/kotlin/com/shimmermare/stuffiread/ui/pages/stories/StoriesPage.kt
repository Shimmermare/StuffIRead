package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.components.story.StoryListWithSearch
import com.shimmermare.stuffiread.ui.pages.story.create.CreateStoryPage
import com.shimmermare.stuffiread.ui.routing.Page

class StoriesPage(
    val presetFilter: StoryFilter = StoryFilter.DEFAULT
) : Page {
    @Composable
    override fun Body() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        onClick = { Router.goTo(CreateStoryPage()) }
                    ) {
                        Icon(Icons.Filled.Add, null)
                    }
                }
            }
        ) {
            StoryListWithSearch(presetFilter)
        }
    }
}