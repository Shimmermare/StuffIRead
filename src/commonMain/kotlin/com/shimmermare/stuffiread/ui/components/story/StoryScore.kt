package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Score
import kotlin.math.roundToInt

/**
 * Displays score as 1 to 5 stars.
 * score -> stars
 * 0 -> 1
 * 0.25 -> 2
 * 0.5 -> 3
 * 0.75 -> 4
 * 1 -> 5
 */
@Composable
fun StoryScore(score: Score) {
    val starCount = remember(score) { 1 + (score.value * 4).roundToInt() }
    Row {
        for (i in 1 .. 5) {
            val filled = i <= starCount
            val color = if (filled) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.secondary.copy(alpha = 0.2F)
            }
            Icon(
                Icons.Filled.Star,
                null,
                modifier = Modifier.height(24.dp),
                tint = color,
            )
        }
    }
}