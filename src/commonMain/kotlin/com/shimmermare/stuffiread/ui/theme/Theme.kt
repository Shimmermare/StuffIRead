package com.shimmermare.stuffiread.ui.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.shimmermare.stuffiread.settings.ThemeBehavior

val LocalTheme: ProvidableCompositionLocal<Theme> = compositionLocalOf { throw IllegalStateException("Theme not set") }

enum class Theme {
    LIGHT,
    DARK;

    companion object {
        val DEFAULT: Theme = LIGHT

        fun fromBehavior(themeBehavior: ThemeBehavior): Theme {
            return when (themeBehavior) {
                ThemeBehavior.FORCE_LIGHT -> LIGHT
                ThemeBehavior.FORCE_DARK -> DARK
                ThemeBehavior.USE_SYSTEM -> SystemThemeProvider.theme
            }
        }
    }
}