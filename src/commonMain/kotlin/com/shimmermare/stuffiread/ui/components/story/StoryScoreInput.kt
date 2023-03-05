package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.shimmermare.stuffiread.settings.ScoreDisplayType
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.ui.appSettings
import com.shimmermare.stuffiread.ui.components.input.OutlinedIntField
import kotlin.math.roundToInt

@Composable
fun StoryScoreInput(value: Score, onValueChange: (Score) -> Unit) {
    when (appSettings.scoreDisplayType) {
        ScoreDisplayType.STARS_5 -> StarsInput(value, 5, onValueChange)
        ScoreDisplayType.STARS_10 -> StarsInput(value, 10, onValueChange)
        ScoreDisplayType.NUMBERS_1_TO_10 -> NumbersInput(value, 10, onValueChange)
        ScoreDisplayType.NUMBERS_1_TO_100 -> NumbersInput(value, 100, onValueChange)
    }
}

@Composable
private fun StarsInput(score: Score, starCount: Int, onValueChange: (Score) -> Unit) {
    val starSize = max(120.dp / starCount, 20.dp)
    val filledStars = remember(score) { 1 + (score.value * (starCount - 1)).roundToInt() }
    Row {
        for (index in 1..starCount) {
            val filled = index <= filledStars
            val color = if (filled) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.secondary.copy(alpha = 0.2F)
            }
            Icon(
                Icons.Filled.Star,
                null,
                modifier = Modifier.size(starSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = starSize / 2),
                        onClick = {
                            val newScore = Score((index - 1) / (starCount.toFloat() - 1))
                            onValueChange(newScore)
                        }
                    ),
                tint = color,
            )
        }
    }
}

@Composable
private fun NumbersInput(score: Score, maxScore: Int, onValueChange: (Score) -> Unit) {
    val scoreMapped = remember(score) { 1 + (score.value * (maxScore - 1)).roundToInt() }

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedIntField(
            value = scoreMapped,
            onValueChange = {
                val newScore = Score((it - 1) / (maxScore.toFloat() - 1))
                onValueChange(newScore)
            },
            modifier = Modifier.width((20 + maxScore.toString().length * 10).dp),
            range = 1..maxScore
        )
        Text("/ $maxScore")
    }
}