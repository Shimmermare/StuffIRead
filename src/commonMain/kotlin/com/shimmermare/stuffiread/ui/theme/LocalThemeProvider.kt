package com.shimmermare.stuffiread.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.settings.ThemeBehavior
import com.shimmermare.stuffiread.ui.appSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

val LocalTheme: ProvidableCompositionLocal<Theme> = compositionLocalOf { throw IllegalStateException("Theme not set") }
val theme: Theme @Composable get() = LocalTheme.current

private val SYSTEM_THEME_SYNC_INTERVAL = 5000.milliseconds

@Composable
fun LocalThemeProvider(content: @Composable () -> Unit) {
    val appSettings = appSettings

    var theme: Theme by remember(appSettings) { mutableStateOf(Theme.fromBehavior(appSettings.themeBehavior)) }

    LaunchedEffect(appSettings) {
        if (appSettings.themeBehavior == ThemeBehavior.USE_SYSTEM) {
            while (isActive) {
                theme = Theme.fromBehavior(ThemeBehavior.USE_SYSTEM)
                delay(SYSTEM_THEME_SYNC_INTERVAL)
            }
        }
    }

    CompositionLocalProvider(LocalTheme provides theme) {
        content()
    }
}
