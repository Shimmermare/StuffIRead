package com.shimmermare.stuffiread.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * For some reason compose material theme doesn't include some basic colors such as warning, success, or info.
 */
data class ExtendedColors(
    val warning: Color,
    val success: Color,
    val info: Color,
    val onWarning: Color,
    val onSuccess: Color,
    val onInfo: Color,
)

val LightColors = lightColors(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF9C27B0),
    error = Color(0xFFD32F2F),
)
val LightExtendedColors = ExtendedColors(
    warning = Color(0xFFFFA726),
    success = Color(0xFF66BB6A),
    info = Color(0xFF29B6F6),
    onWarning = Color.White,
    onSuccess = Color.White,
    onInfo = Color.White,
)

val DarkColors = darkColors(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF9C27B0),
    error = Color(0xFFD32F2F),
)
val DarkExtendedColors = ExtendedColors(
    warning = Color(0xFFFFA726),
    success = Color(0xFF66BB6A),
    info = Color(0xFF29B6F6),
    onWarning = Color.Black,
    onSuccess = Color.Black,
    onInfo = Color.Black,
)

val LocalExtendedColors: ProvidableCompositionLocal<ExtendedColors> = compositionLocalOf { LightExtendedColors }

val MaterialTheme.extendedColors: ExtendedColors @Composable get() = LocalExtendedColors.current
