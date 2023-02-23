package com.shimmermare.stuffiread.settings

data class AppSettings(
    val themeBehavior: ThemeBehavior = DEFAULT_THEME_BEHAVIOR,
    val scoreDisplayType: ScoreDisplayType = DEFAULT_SCORE_DISPLAY_TYPE
) {
    companion object {
        val DEFAULT_THEME_BEHAVIOR = ThemeBehavior.USE_SYSTEM
        val DEFAULT_SCORE_DISPLAY_TYPE = ScoreDisplayType.STARS_10
    }
}

enum class ThemeBehavior {
    USE_SYSTEM,
    FORCE_LIGHT,
    FORCE_DARK
}

enum class ScoreDisplayType {
    STARS_5,
    STARS_10,
    NUMBERS_1_TO_10,
    NUMBERS_1_TO_100
}