package com.shimmermare.stuffiread.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

fun Modifier.dashedBorder(width: Dp, radius: Dp, color: Color, dashLength: Float = 5f, period: Float = 5f): Modifier {
    return drawBehind {
        drawIntoCanvas {
            val paint = Paint()
                .apply {
                    strokeWidth = width.toPx()
                    this.color = color
                    style = PaintingStyle.Stroke
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, period), 0f)
                }
            it.drawRoundRect(
                left = width.toPx() / 2,
                top = width.toPx() / 2,
                right = size.width - (width.toPx() / 2),
                bottom = size.height - (width.toPx() / 2),
                radiusX = radius.toPx(),
                radiusY = radius.toPx(),
                paint = paint
            )
        }
    }
}