package com.shimmermare.stuffiread.ui.components.text

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * Used for important names. E.g. tag name.
 */
@Composable
fun FilledNameText(
    text: String,
    color: Color,
    singleLine: Boolean = false,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    modifier: Modifier? = null,
) {
    DisableSelection {
        FilledText(
            text = text,
            color = color,
            modifier = (modifier ?: Modifier)
                .height(height ?: 30.dp),
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            fontSize = fontSize ?: MaterialTheme.typography.subtitle1.fontSize,
        )
    }
}