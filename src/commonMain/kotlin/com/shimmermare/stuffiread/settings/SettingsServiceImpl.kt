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
                ?: ThemeBehavior.USE_SYSTEM
        )
    }

    private fun updateToSource() {
        current.let {
            settingsSource[THEME_BEHAVIOR_KEY] = it.themeBehavior.name
        }
    }

    companion object {
        private const val THEME_BEHAVIOR_KEY = "themeBehavior"
    }
}