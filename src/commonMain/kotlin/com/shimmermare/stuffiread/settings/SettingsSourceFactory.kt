package com.shimmermare.stuffiread.settings

import com.russhwolf.settings.Settings

/**
 * Platform independent settings source provider.
 */
expect object SettingsSourceFactory {
    fun create(): Settings
}