package com.shimmermare.stuffiread.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.settings.AppSettingsService
import com.shimmermare.stuffiread.settings.AppSettingsServiceImpl

object AppSettingsHolder {
    private val service: AppSettingsService = AppSettingsServiceImpl()

    var settings: AppSettings by mutableStateOf(service.getSettings())
        private set

    fun update(settings: AppSettings) {
        service.updateSettings(settings)
        this.settings = settings
    }

    fun reset(): AppSettings {
        return service.resetSettings().also { this.settings = it }
    }
}