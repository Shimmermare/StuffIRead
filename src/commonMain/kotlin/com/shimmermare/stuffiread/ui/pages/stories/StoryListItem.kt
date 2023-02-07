package com.shimmermare.stuffiread.ui.pages.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.domain.stories.Story

@Composable
fun StoryListItem(story: Story, onChange: ((Story) -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = story.name)
        story.description?.let { Text(text = it) }
    }
}