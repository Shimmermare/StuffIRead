package com.shimmermare.stuffiread.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_SCORE_DISPLAY_TYPE
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_THEME_BEHAVIOR
import com.shimmermare.stuffiread.settings.AppSettings.Companion.RECENTLY_OPENED_TO_KEEP
import com.shimmermare.stuffiread.util.AppJson
import com.shimmermare.stuffiread.util.PathSerializer
import io.github.aakira.napier.Napier
import kotlinx.serialization.builtins.ListSerializer

class AppSettingsServiceImpl : AppSettingsService {
    private val settingsSource: Settings = SettingsSourceFactory.create()

    @Volatile
    private lateinit var current: AppSettings

    init {
        synchronized(settingsSource) {
            loadFromSource()
        }
    }

    override fun getSettings(): AppSettings {
        synchronized(settingsSource) {
            return current
        }
    }

    override fun updateSettings(settings: AppSettings) {
        synchronized(settingsSource) {
            current = settings
            updateToSource()
        }
    }

    override fun resetSettings(): AppSettings {
        synchronized(settingsSource) {
            current = AppSettings()
            updateToSource()
            return current
        }
    }

    /**
     * Any failure to read from source should be captured and result in default value for setting.
     * Otherwise, app won't even start.
     */
    private fun loadFromSource() {
        val themeBehavior = getFromSourceAndParseOrDefault(THEME_BEHAVIOR_KEY, DEFAULT_THEME_BEHAVIOR) {
            ThemeBehavior.valueOf(it)
        }
        val scoreDisplayType = getFromSourceAndParseOrDefault(SCORE_DISPLAY_TYPE_KEY, DEFAULT_SCORE_DISPLAY_TYPE) {
            ScoreDisplayType.valueOf(it)
        }
        val recentlyOpenedArchives = getFromSourceAndParseOrDefault(RECENTLY_OPENED_ARCHIVES_KEY, emptyList()) {
            AppJson.decodeFromString(ListSerializer(PathSerializer), it).take(RECENTLY_OPENED_TO_KEEP.toInt())
        }

        current = AppSettings(
            themeBehavior = themeBehavior,
            scoreDisplayType = scoreDisplayType,
            recentlyOpenedArchives = recentlyOpenedArchives
        )
    }

    private inline fun <T> getFromSourceAndParseOrDefault(
        key: String,
        default: T,
        crossinline parse: (String) -> T
    ): T {
        val fromSource = try {
            settingsSource.get<String>(key)
        } catch (e: Exception) {
            Napier.e("Failed to get setting $key value from source", e)
            return default
        }

        return if (fromSource == null) {
            default
        } else {
            try {
                parse(fromSource)
            } catch (e: Exception) {
                Napier.e("Failed to parse setting $key value from source: $fromSource", e)
                default
            }
        }
    }

    private fun updateToSource() {
        current.let {
            settingsSource[THEME_BEHAVIOR_KEY] = it.themeBehavior.name
            settingsSource[SCORE_DISPLAY_TYPE_KEY] = it.scoreDisplayType.name
            settingsSource[RECENTLY_OPENED_ARCHIVES_KEY] =
                AppJson.encodeToString(ListSerializer(PathSerializer), it.recentlyOpenedArchives)
        }
    }

    companion object {
        private const val THEME_BEHAVIOR_KEY = "theme_behavior"
        private const val SCORE_DISPLAY_TYPE_KEY = "score_display_type"
        private const val RECENTLY_OPENED_ARCHIVES_KEY = "recently_opened_archives"
    }
}