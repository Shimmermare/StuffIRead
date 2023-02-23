package com.shimmermare.stuffiread.settings

data class AppSettings(
    val themeBehavior: ThemeBehavior = ThemeBehavior.USE_SYSTEM,
)

enum class ThemeBehavior {
    USE_SYSTEM,
    FORCE_LIGHT,
    FORCE_DARK
}