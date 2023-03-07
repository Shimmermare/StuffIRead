package com.shimmermare.stuffiread.ui.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun FilledText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    fontSize: TextUnit = TextUnit.Unspecified,
    textStyle: TextStyle = TextStyle.Default.copy(
        color = Color.White,
        shadow = Shadow(color = Color.Black, blurRadius = 0.5F)
    )
) {
    Box(
        modifier = Modifier.background(color, shape = RoundedCornerShape(5.dp)).then(modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).then(textModifier),
            maxLines = maxLines,
            fontSize = fontSize,
            style = textStyle,
        )
    }
}