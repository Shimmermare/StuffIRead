package com.shimmermare.stuffiread.ui.util

import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

private val unitsBase10 = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
private val unitsBase2 = arrayOf("B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB")

fun UInt.toHumanReadableBytes(base2: Boolean = false, precision: Int = 1): String {
    return this.toLong().toHumanReadableBytes(base2, precision)
}

fun Long.toHumanReadableBytes(base2: Boolean = false, precision: Int = 1): String {
    require(this >= 0) {
        "Negative sizes are not supported"
    }
    val magnitudeStep: Double = if (base2) 1024.0 else 1000.0
    val units = if (base2) unitsBase2 else unitsBase10
    if (this < magnitudeStep) return "$this ${units[0]}"

    val index = floor(ln(this.toDouble()) / ln(magnitudeStep)).toInt()
    val value = (this / magnitudeStep.pow(index))
        .toBigDecimal()
        .setScale(precision, RoundingMode.FLOOR)
        .toDouble()
        .toString()

    val valueStrTrimmed = if (value.indexOf('.') > 0) value.trimEnd('.', '0') else value
    return "$valueStrTrimmed ${units[index]}"
}