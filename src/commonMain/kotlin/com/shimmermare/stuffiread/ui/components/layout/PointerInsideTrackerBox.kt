package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

/**
 * Use this to know when pointer is inside the box.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
inline fun PointerInsideTrackerBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    crossinline content: @Composable (pointerInside: Boolean) -> Unit
) {
    var pointerInside by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { pointerInside = true }
            .onPointerEvent(PointerEventType.Exit) { pointerInside = false },
        contentAlignment = contentAlignment,
    ) {
        content(pointerInside)
    }
}