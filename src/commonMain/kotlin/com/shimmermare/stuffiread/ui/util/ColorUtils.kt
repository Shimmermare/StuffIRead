package com.shimmermare.stuffiread.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object ColorUtils {
    val Color.alphaInt: Int get() = (this.toArgb().toLong() and 0xFF000000 shr 24).toInt()
    val Color.redInt: Int get() = this.toArgb() and 0x00FF0000 shr 16
    val Color.greenInt: Int get() = this.toArgb() and 0x0000FF00 shr 8
    val Color.blueInt: Int get() = this.toArgb() and 0x000000FF

    fun Color.with(
        red: Int = this.redInt,
        green: Int = this.greenInt,
        blue: Int = this.blueInt,
        alpha: Int = this.alphaInt
    ): Color {
        val argb = (alpha.toLong() shl 24 and 0xFF000000).toInt() or
                (red shl 16 and 0x00FF0000) or
                (green shl 8 and 0x0000FF00) or
                (blue and 0x000000FF)
        return Color(argb)
    }

    fun String.parseHexColor(): Color {
        val hex = this.removePrefix("#").lowercase()
        if (hex.length != 6 || hex.any { it !in '0'..'9' && it !in 'a'..'f' }) {
            throw IllegalArgumentException("Invalid hex color: expected 6 hex numbers")
        }
        return Color(("ff$hex").toLong(16))
    }

    fun Color.toHexColor(): String {
        val argb = toArgb()
        return "#" +
                (argb and 0x00FF0000 shr 16).toString(16).padStart(2, '0') +
                (argb and 0x0000FF00 shr 8).toString(16).padStart(2, '0') +
                (argb and 0x000000FF).toString(16).padStart(2, '0')
    }
}