package com.shimmermare.stuffiread.ui.routing

import androidx.compose.runtime.Composable

/**
 * Navigable page.
 * Usually implemented as object and data as data class.
 */
interface Page {
    /**
     * Override if title is needed (e.g. story name).
     */
    @Composable
    fun Title() {
    }

    /**
     * Page body content.
     */
    @Composable
    fun Body()
}