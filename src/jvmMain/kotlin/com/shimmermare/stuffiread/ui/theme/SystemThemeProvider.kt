package com.shimmermare.stuffiread.ui.theme

import io.github.aakira.napier.Napier
import java.util.concurrent.TimeUnit

actual object SystemThemeProvider {
    actual val theme: Theme get() = tryGetSystemTheme()

    private fun tryGetSystemTheme(): Theme {
        val os = System.getProperty("os.name")
        return when {
            os.startsWith("Win", ignoreCase = true) -> tryGetWindowsTheme()
            os.equals("Mac OS X", ignoreCase = true) -> tryGetMacOSTheme()
            os.startsWith("Linux", ignoreCase = true) -> tryGetLinuxTheme()
            else -> Theme.DEFAULT
        }
    }

    private fun tryGetWindowsTheme(): Theme {
        return tryUsingQueryCmd(
            "reg",
            "query",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
            "/v",
            "AppsUseLightTheme"
        ) {
            it.contains("AppsUseLightTheme    REG_DWORD    0x0")
        }
    }

    private fun tryGetMacOSTheme(): Theme {
        return tryUsingQueryCmd(
            "defaults",
            "read",
            "-g",
            "/v",
            "AppleInterfaceStyle"
        ) {
            it.contains("Dark")
        }
    }

    private fun tryGetLinuxTheme(): Theme {
        // Assume gnome - no support for other stuff yet
        return tryUsingQueryCmd(
            "bash",
            "-c",
            "[[ `gsettings get org.gnome.desktop.interface color-scheme` =~ 'dark' ]] && echo true || echo false"
        ) {
            it.contains("true")
        }
    }

    private fun tryUsingQueryCmd(vararg command: String, isDarkTheme: (String) -> Boolean): Theme {
        return try {
            val process = ProcessBuilder(*command).start()

            process.waitFor(5, TimeUnit.SECONDS)

            val query = String(process.inputStream.readAllBytes(), Charsets.UTF_8)
            return if (isDarkTheme(query)) {
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