package com.shimmermare.stuffiread.ui.components.text

import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

/**
 * Used for important names. E.g. tag name.
 */
@Composable
fun FilledNameText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    DisableSelection {
        FilledText(
            text = text,
            color = color,
            modifier = modifier,
            maxLines = 1,
            fontSize = fontSize,
        )
    }
}