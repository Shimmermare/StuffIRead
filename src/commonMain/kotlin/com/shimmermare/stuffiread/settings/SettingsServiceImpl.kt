package com.shimmermare.stuffiread.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SettingsServiceImpl : SettingsService {
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

    private fun loadFromSource() {
        current = AppSettings(
            themeBehavior = settingsSource.get<String>(THEME_BEHAVIOR_KEY)?.let { ThemeBehavior.valueOf(it) }
                ?: AppSettings.DEFAULT_THEME_BEHAVIOR,
            scoreDisplayType = settingsSource.get<String>(SCORE_DISPLAY_TYPE_KEY)?.let { ScoreDisplayType.valueOf(it) }
                ?: AppSettings.DEFAULT_SCORE_DISPLAY_TYPE
        )
    }

    private fun updateToSource() {
        current.let {
            settingsSource[THEME_BEHAVIOR_KEY] = it.themeBehavior.name
            settingsSource[SCORE_DISPLAY_TYPE_KEY] = it.scoreDisplayType.name
        }
    }

    companion object {
        private const val THEME_BEHAVIOR_KEY = "theme_behavior"
        private const val SCORE_DISPLAY_TYPE_KEY = "score_display_type"
    }
}