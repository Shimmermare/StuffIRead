package com.shimmermare.stuffiread.settings

interface AppSettingsService {
    fun getSettings(): AppSettings

    fun updateSettings(settings: AppSettings)

    /**
     * Reset settings to default [AppSettings] values.
     * @return default settings.
     */
    fun resetSettings(onlyUserSettings: Boolean = true): AppSettings
}