package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Differences from regular [TooltipArea]:
 * 1. Tooltip has filled background and padding.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtendedTooltipArea(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TooltipArea(
        tooltip = {
            Surface(elevation = 2.dp) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .background(MaterialTheme.colors.surface),
                ) {
                    tooltip()
                }
            }
        },
        modifier = modifier,
        content = content
    )
}