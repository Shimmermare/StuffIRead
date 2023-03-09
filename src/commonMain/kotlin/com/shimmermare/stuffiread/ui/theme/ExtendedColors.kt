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
    primary = Color(0xFF2196F3), // Blue 500
    primaryVariant = Color(0xFF3F51B5), // Indigo 500
    secondary = Color(0xFF009688), // Teal 500
    error = Color(0xFFEF5350), // Red 400
)
val LightExtendedColors = ExtendedColors(
    warning = Color(0xFFFFA726), // Orange 400
    success = Color(0xFF66BB6A), // Green 400
    info = Color(0xFF29B6F6), // Light Blue 400
    onWarning = Color.White,
    onSuccess = Color.White,
    onInfo = Color.White,
)

val DarkColors = darkColors(
    primary = Color(0xFF1976D2), // Blue 700
    primaryVariant = Color(0xFF303F9F), // Indigo 700
    secondary = Color(0xFF00796B), // Teal 700
    error = Color(0xFFE53935), // Red 600
)
val DarkExtendedColors = ExtendedColors(
    warning = Color(0xFFFB8C00), // Orange 600
    success = Color(0xFF43A047), // Green 600
    info = Color(0xFF039BE5), // Light Blue 600
    onWarning = Color.Black,
    onSuccess = Color.Black,
    onInfo = Color.Black,
)

val LocalExtendedColors: ProvidableCompositionLocal<ExtendedColors> = compositionLocalOf { LightExtendedColors }

val MaterialTheme.extendedColors: ExtendedColors @Composable get() = LocalExtendedColors.current
