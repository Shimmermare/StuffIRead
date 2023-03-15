package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun ChipVerticalGrid(
    modifier: Modifier = Modifier,
    spacing: Dp = 5.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val spacingValue = spacing.toPx().toInt()

        var totalHeight = 0
        // Rows where the sum of placeable widths + spacing between them is less than constraints.maxWidth
        val rows: MutableList<ChipRow> = mutableListOf()

        var currentRowWidth = 0
        var currentRowHeight = 0
        var currentRow: MutableList<Placeable> = mutableListOf()

        fun processPlaceable(placeable: Placeable): Boolean? {
            val newRowHeight = max(currentRowHeight, placeable.height)

            val newTotalHeight = totalHeight + (if (rows.isEmpty()) 0 else spacingValue) + newRowHeight
            if (newTotalHeight > constraints.maxHeight) {
                if (currentRow.isNotEmpty()) {
                    totalHeight += (if (rows.isEmpty()) 0 else spacingValue) + currentRowHeight
                    rows.add(ChipRow(currentRowHeight, currentRow))
                }
                return false
            }

            currentRowHeight = newRowHeight

            val widthToAdd = (if (currentRow.isEmpty()) 0 else spacingValue) + placeable.width

            val rowIsFull = currentRowWidth + widthToAdd > constraints.maxWidth
            if (rowIsFull) {
                totalHeight = newTotalHeight
                rows.add(ChipRow(currentRowHeight, currentRow))
                currentRowWidth = 0
                currentRowHeight = 0
                currentRow = mutableListOf()

                // This index will be processed again but with new row
                return null
            }

            currentRowWidth += widthToAdd
            currentRow.add(placeable)
            return true
        }

        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)

            var result = processPlaceable(placeable)
            while (result == null) {
                result = processPlaceable(placeable)
            }

            if (result) {
                continue
            } else {
                break
            }
        }

        if (currentRow.isNotEmpty()) {
            totalHeight += (if (rows.isEmpty()) 0 else spacingValue) + currentRowHeight
            rows.add(ChipRow(currentRowHeight, currentRow))
        }

        layout(
            width = constraints.maxWidth,
            height = totalHeight
        ) {
            var currentY = 0
            rows.forEachIndexed { rowIndex, row ->
                var currentX = 0
                row.placeables.forEachIndexed { placeableIndex, placeable ->
                    placeable.place(currentX, currentY)
                    currentX += placeable.width
                    if (placeableIndex + 1 < row.placeables.size) {
                        currentX += spacingValue
                    }
                }
                currentY += row.height
                if (rowIndex + 1 < rows.size) {
                    currentY += spacingValue
                }
            }
        }
    }
}

private data class ChipRow(
    val height: Int,
    val placeables: List<Placeable>
)