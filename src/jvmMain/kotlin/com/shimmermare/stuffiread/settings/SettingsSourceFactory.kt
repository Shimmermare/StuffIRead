package com.shimmermare.stuffiread.settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

/**
 * JVM settings source implementation uses [Preferences].
 */
actual object SettingsSourceFactory {
    private const val PREFERENCES_NODE = "Shimmermare/StuffIRead"

    actual fun create(): Settings {
        val userRoot = Preferences.userRoot()
        val preferences = userRoot.node(PREFERENCES_NODE)
        return PreferencesSettings(preferences)
    }
}