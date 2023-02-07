package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Border for popup with border and background.
 */
@Composable
inline fun PopupContent(crossinline content: @Composable () -> Unit) {
    Surface(elevation = 24.dp) {
        Box(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(1))
                .background(color = MaterialTheme.colors.surface),
        ) {
            content()
        }
    }
}