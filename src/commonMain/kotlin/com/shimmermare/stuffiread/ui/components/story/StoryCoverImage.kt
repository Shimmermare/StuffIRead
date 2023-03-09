package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyCoverService
import com.shimmermare.stuffiread.ui.components.layout.OptionalLoadingContainer
import io.github.aakira.napier.Napier

@Composable
fun StoryCoverImage(
    storyId: StoryId,
    modifier: Modifier = Modifier.height(150.dp).widthIn(min = 100.dp, max = 250.dp),
) {
    // Hide box after loading is finished when there's no cover
    var hide: Boolean by remember(storyId) { mutableStateOf(false) }

    if (!hide) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            OptionalLoadingContainer(
                key = storyId,
                loader = {
                    storyCoverService.getStoryCover(storyId)?.let {
                        BitmapPainter(loadImageBitmap(it.data.inputStream()))
                    }
                },
                onError = {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Failed to load cover", color = MaterialTheme.colors.error)
                    }
                    Napier.e(it) { "Failed to load cover for $storyId" }
                }
            ) { cover ->
                if (cover != null) {
                    Image(
                        painter = cover,
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight()
                    )
                } else {
                    hide = true
                }
            }
        }
    }
}