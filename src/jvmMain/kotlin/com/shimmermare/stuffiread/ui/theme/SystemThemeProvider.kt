package com.shimmermare.stuffiread.ui.theme

import io.github.aakira.napier.Napier
import org.apache.commons.lang3.SystemUtils
import java.util.concurrent.TimeUnit

actual object SystemThemeProvider {
    actual val theme: Theme
        get() {
            return when {
                SystemUtils.IS_OS_WINDOWS -> tryGetWindowsTheme()
                // TODO: Support MacOS
                else -> Theme.DEFAULT
            }
        }

    private fun tryGetWindowsTheme(): Theme {
        return try {
            val process = ProcessBuilder(
                "reg",
                "query",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "/v",
                "AppsUseLightTheme"
            ).start()

            process.waitFor(5, TimeUnit.SECONDS)

            val query = String(process.inputStream.readAllBytes(), Charsets.UTF_8)
            return if (query.contains("AppsUseLightTheme    REG_DWORD    0x0")) {
                Theme.DARK
            } else {
                Theme.LIGHT
            }
        } catch (e: Exception) {
            Napier.e(e) { "Failed to get system theme" }
            Theme.DEFAULT
        }
    }
}