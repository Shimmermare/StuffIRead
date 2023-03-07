package com.shimmermare.stuffiread.settings

import com.shimmermare.stuffiread.settings.AppSettings.Companion.RECENTLY_OPENED_TO_KEEP
import java.nio.file.Path

data class AppSettings(
    val themeBehavior: ThemeBehavior = DEFAULT_THEME_BEHAVIOR,
    val scoreDisplayType: ScoreDisplayType = DEFAULT_SCORE_DISPLAY_TYPE,
    val openLastArchiveOnStartup: Boolean = DEFAULT_OPEN_LAST_ARCHIVE_ON_STARTUP,
    val enablePonyIntegrations: Boolean = DEFAULT_ENABLE_PONY_INTEGRATIONS,
    /**
     * Ordered from most to least recent.
     * Contains no more than [RECENTLY_OPENED_TO_KEEP] paths and contains no duplicates.
     */
    val recentlyOpenedArchives: List<Path> = emptyList(),
) {
    init {
        require(recentlyOpenedArchives.size.toUInt() <= RECENTLY_OPENED_TO_KEEP) {
            "${recentlyOpenedArchives.size} recently opened archives is more than allowed to keep ($RECENTLY_OPENED_TO_KEEP)"
        }
        require(recentlyOpenedArchives.toSet().size == recentlyOpenedArchives.size) {
            "Duplicates are not allowed in recently opened archives: ${recentlyOpenedArchives - recentlyOpenedArchives.toSet()}"
        }
    }

    companion object {
        val DEFAULT_THEME_BEHAVIOR = ThemeBehavior.USE_SYSTEM
        val DEFAULT_SCORE_DISPLAY_TYPE = ScoreDisplayType.STARS_10
        const val DEFAULT_OPEN_LAST_ARCHIVE_ON_STARTUP = true
        const val DEFAULT_ENABLE_PONY_INTEGRATIONS = true

        const val RECENTLY_OPENED_TO_KEEP: UInt = 10u
    }
}

enum class ThemeBehavior {
    USE_SYSTEM,
    FORCE_LIGHT,
    FORCE_DARK;

    companion object {
        // To avoid alloc
        val values: Set<ThemeBehavior> = values().toSet()
    }
}

enum class ScoreDisplayType {
    STARS_5,
    STARS_10,
    NUMBERS_1_TO_10,
    NUMBERS_1_TO_100;

    companion object {
        // To avoid alloc
        val values: Set<ScoreDisplayType> = values().toSet()
    }
}