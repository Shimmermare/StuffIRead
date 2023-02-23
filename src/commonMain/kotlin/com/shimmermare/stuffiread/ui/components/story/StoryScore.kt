package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.shimmermare.stuffiread.settings.ScoreDisplayType
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.ui.AppState
import kotlin.math.roundToInt

@Composable
fun StoryScore(app: AppState, score: Score) {
    StoryScore(app.settingsService.getSettings().scoreDisplayType, score)
}

@Composable
fun StoryScore(displayType: ScoreDisplayType, score: Score) {
    when (displayType) {
        ScoreDisplayType.STARS_5 -> StarsScore(score, 5)
        ScoreDisplayType.STARS_10 -> StarsScore(score, 10)
        ScoreDisplayType.NUMBERS_1_TO_10 -> NumbersScore(score, 10)
        ScoreDisplayType.NUMBERS_1_TO_100 -> NumbersScore(score, 100)
    }
}

@Composable
private fun StarsScore(score: Score, starCount: Int) {
    val filledStars = remember(score) { 1 + (score.value * (starCount - 1)).roundToInt() }
    Row {
        for (i in 1..starCount) {
            val filled = i <= filledStars
            val color = if (filled) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.secondary.copy(alpha = 0.2F)
            }
            Icon(
                Icons.Filled.Star,
                null,
                modifier = Modifier.size(max(120.dp / starCount, 18.dp)),
                tint = color,
            )
        }
    }
}

@Composable
private fun NumbersScore(score: Score, maxScore: Int) {
    val scoreMapped = remember(score) { 1 + (score.value * (maxScore - 1)).roundToInt() }
    Text("$scoreMapped/$maxScore")
}