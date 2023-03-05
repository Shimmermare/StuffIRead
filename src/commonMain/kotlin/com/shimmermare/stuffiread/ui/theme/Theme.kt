package com.shimmermare.stuffiread.ui.theme

import com.shimmermare.stuffiread.settings.ThemeBehavior

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