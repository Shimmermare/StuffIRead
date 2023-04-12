package com.shimmermare.stuffiread.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_CHECK_UPDATES
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_ENABLE_PONY_INTEGRATIONS
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_OPEN_LAST_ARCHIVE_ON_STARTUP
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_SCORE_DISPLAY_TYPE
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_SHOW_FOREIGN_IMPORT_SOURCES
import com.shimmermare.stuffiread.settings.AppSettings.Companion.DEFAULT_THEME_BEHAVIOR
import com.shimmermare.stuffiread.settings.AppSettings.Companion.RECENTLY_OPENED_TO_KEEP
import com.shimmermare.stuffiread.util.AppJson
import com.shimmermare.stuffiread.util.PathSerializer
import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.toTag
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

    override fun resetSettings(onlyUserSettings: Boolean): AppSettings {
        synchronized(settingsSource) {
            current = if (onlyUserSettings) current.copyAndResetUserSettings() else AppSettings()
            updateToSource()
            return current
        }
    }

    /**
     * Any failure to read from source should be captured and result in default value for setting.
     * Otherwise, app won't even start.
     */
    private fun loadFromSource() {
        val locale = getFromSourceOrNull<String>(LOCALE_KEY)?.let {
            val stored = forLocaleTag(it)
            if (Strings.locales.contains(stored)) {
                stored
            } else {
                Napier.w { "Can't use locale $stored from settings because it's unsupported. Default will be used." }
                null
            }
        }
        val openLastArchiveOnStartup = getFromSourceOrDefault(
            OPEN_LAST_ARCHIVE_ON_STARTUP_KEY, DEFAULT_OPEN_LAST_ARCHIVE_ON_STARTUP
        )
        val recentlyOpenedArchives = getFromSourceAndParseOrDefault(RECENTLY_OPENED_ARCHIVES_KEY, emptyList()) {
            AppJson.decodeFromString(ListSerializer(PathSerializer), it).take(RECENTLY_OPENED_TO_KEEP.toInt())
        }
        val checkUpdates = getFromSourceOrDefault(CHECK_UPDATES_KEY, DEFAULT_CHECK_UPDATES)
        val ignoreVersion = getFromSourceOrNull<String>(IGNORE_VERSION_KEY)

        val themeBehavior = getFromSourceAndParseOrDefault(THEME_BEHAVIOR_KEY, DEFAULT_THEME_BEHAVIOR) {
            ThemeBehavior.valueOf(it)
        }
        val scoreDisplayType = getFromSourceAndParseOrDefault(SCORE_DISPLAY_TYPE_KEY, DEFAULT_SCORE_DISPLAY_TYPE) {
            ScoreDisplayType.valueOf(it)
        }

        val showForeignImportSources = getFromSourceOrDefault(
            SHOW_FOREIGN_IMPORT_SOURCES_KEY, DEFAULT_SHOW_FOREIGN_IMPORT_SOURCES
        )
        val ponyIntegrations = getFromSourceOrDefault(PONY_INTEGRATIONS_KEY, DEFAULT_ENABLE_PONY_INTEGRATIONS)

        current = AppSettings(
            locale = locale,
            openLastArchiveOnStartup = openLastArchiveOnStartup,
            recentlyOpenedArchives = recentlyOpenedArchives,
            checkUpdates = checkUpdates,
            ignoreVersion = ignoreVersion,
            themeBehavior = themeBehavior,
            scoreDisplayType = scoreDisplayType,
            showForeignImportSources = showForeignImportSources,
            enablePonyIntegrations = ponyIntegrations,
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

    private inline fun <reified T : Any> getFromSourceOrDefault(key: String, default: T): T {
        return try {
            settingsSource[key] ?: default
        } catch (e: Exception) {
            Napier.e("Failed to get setting $key value from source", e)
            return default
        }
    }

    private inline fun <reified T : Any> getFromSourceOrNull(key: String): T? {
        return try {
            settingsSource[key]
        } catch (e: Exception) {
            Napier.e("Failed to get setting $key value from source", e)
            return null
        }
    }

    private fun updateToSource() {
        current.let {
            settingsSource[LOCALE_KEY] = it.locale?.toTag()
            settingsSource[OPEN_LAST_ARCHIVE_ON_STARTUP_KEY] = it.openLastArchiveOnStartup
            settingsSource[RECENTLY_OPENED_ARCHIVES_KEY] = AppJson.encodeToString(
                ListSerializer(PathSerializer), it.recentlyOpenedArchives
            )
            settingsSource[CHECK_UPDATES_KEY] = it.checkUpdates
            settingsSource[IGNORE_VERSION_KEY] = it.ignoreVersion

            settingsSource[THEME_BEHAVIOR_KEY] = it.themeBehavior.name
            settingsSource[SCORE_DISPLAY_TYPE_KEY] = it.scoreDisplayType.name

            settingsSource[SHOW_FOREIGN_IMPORT_SOURCES_KEY] = it.showForeignImportSources
            settingsSource[PONY_INTEGRATIONS_KEY] = it.enablePonyIntegrations
        }
    }

    companion object {
        private const val LOCALE_KEY = "Locale"
        private const val OPEN_LAST_ARCHIVE_ON_STARTUP_KEY = "OpenLastArchiveOnStartup"
        private const val RECENTLY_OPENED_ARCHIVES_KEY = "RecentlyOpenedArchives"
        private const val CHECK_UPDATES_KEY = "CheckUpdates"
        private const val IGNORE_VERSION_KEY = "IgnoreVersion"

        private const val THEME_BEHAVIOR_KEY = "ThemeBehavior"
        private const val SCORE_DISPLAY_TYPE_KEY = "ScoreDisplayType"

        private const val SHOW_FOREIGN_IMPORT_SOURCES_KEY = "ShowForeignImportSources"
        private const val PONY_INTEGRATIONS_KEY = "EnablePonyIntegrations"
    }
}