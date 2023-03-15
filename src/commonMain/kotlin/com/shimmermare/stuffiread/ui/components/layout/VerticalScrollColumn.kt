package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
inline fun VerticalScrollColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    crossinline content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    Layout(
        modifier = modifier,
        content = {
            Column(
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                modifier = Modifier.verticalScroll(scrollState).padding(end = 12.dp)
            ) {
                content()
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    ) { measurables, constraints ->
        val column = measurables[0].measure(constraints)
        val scrollbar = measurables[1].measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = column.height
            )
        )

        layout(
            width = column.width,
            height = column.height
        ) {
            column.place(0, 0)
            scrollbar.place(column.width - scrollbar.width, 0)
        }
    }
}