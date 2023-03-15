package com.shimmermare.stuffiread.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.util.LocalWindowSize
import io.github.aakira.napier.Napier
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

@Composable
inline fun AwtWindowSizeProvider(window: Window, crossinline content: @Composable () -> Unit) {
    var windowSize by remember { mutableStateOf(window.size.run { DpSize(width.dp, height.dp) }) }

    DisposableEffect(window) {
        val listener = object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val newWindowSize = window.size.run { DpSize(width.dp, height.dp) }
                if (newWindowSize != windowSize) {
                    windowSize = newWindowSize
                    Napier.d { "Resized window: $windowSize" }
                }
            }
        }

        window.addComponentListener(listener)

        onDispose {
            window.removeComponentListener(listener)
        }
    }

    CompositionLocalProvider(LocalWindowSize provides windowSize) {
        content()
    }
}