package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.Router

object StoriesPage : Page<EmptyData> {
    override val name = "Stories"

    @Composable
    override fun renderBody(router: Router, app: AppState, data: EmptyData) {
        val stories = remember { app.storyService.getAllStories() }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Stories",
                style = MaterialTheme.typography.h6
            )
            LazyColumn {
                items(stories) { tagCategory ->
                    StoryListItem(tagCategory, onChange = {})
                }
            }
        }
    }
}