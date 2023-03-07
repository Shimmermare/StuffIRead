package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.shimmermare.stuffiread.ui.theme.Theme
import com.shimmermare.stuffiread.ui.theme.theme

/**
 * Popup that fills entire screen and displays content in the center.
 * Visibility is handled externally.
 */
@Composable
inline fun FullscreenPopup(
    crossinline content: @Composable () -> Unit
) {
    Popup(
        popupPositionProvider = NoopPopupPositionProvider,
        focusable = true,
    ) {
        val backgroundColor = if (theme == Theme.DARK) {
            Color.White.copy(alpha = 0.15F)
        } else {
            Color.Black.copy(alpha = 0.6F)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            PopupContent {
                content()
            }
        }
    }
}

object NoopPopupPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset(0, 0)
    }
}