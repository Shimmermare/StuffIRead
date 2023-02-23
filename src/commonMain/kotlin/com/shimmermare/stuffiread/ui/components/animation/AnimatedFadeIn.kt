package com.shimmermare.stuffiread.ui.components.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Fade-in once animation.
 */
@Composable
fun AnimatedFadeIn(key: Any? = null, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = remember(key) { MutableTransitionState(false) }.apply { targetState = true },
        enter = fadeIn()
    ) {
        content()
    }
}