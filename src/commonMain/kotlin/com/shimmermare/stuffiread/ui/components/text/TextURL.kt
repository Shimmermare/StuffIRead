package com.shimmermare.stuffiread.ui.components.text

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextURI(
    url: String,
    text: String = url,
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    val uriHandler = LocalUriHandler.current
    TooltipArea(tooltip = {
        if (url != text) {
            FilledText(url, color = MaterialTheme.colors.surface)
        }
    }) {
        Text(
            text = text,
            style = style,
            fontSize = fontSize,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable { uriHandler.openUri(url) },
        )
    }
}