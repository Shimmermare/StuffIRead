package com.shimmermare.stuffiread.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.AppSettingsHolder.settings
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.theme.DarkColors
import com.shimmermare.stuffiread.ui.theme.DarkExtendedColors
import com.shimmermare.stuffiread.ui.theme.LightColors
import com.shimmermare.stuffiread.ui.theme.LightExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalExtendedColors
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.LocalThemeProvider
import com.shimmermare.stuffiread.ui.theme.Theme
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.AppVersionUtils
import com.shimmermare.stuffiread.util.NewUpdate
import de.comahe.i18n4k.config.I18n4kConfigDefault
import de.comahe.i18n4k.i18n4k
import io.github.aakira.napier.Napier

val Router: Router = Router()

var CurrentLocale by mutableStateOf(i18n4k.locale)
    private set

@Composable
@Preview
fun App() {
    LaunchedEffect(settings.locale) {
        val expectedLocale = settings.locale ?: i18n4k.defaultLocale
        if (i18n4k.locale != expectedLocale) {
            i18n4k = I18n4kConfigDefault().apply { locale = expectedLocale }
        }
        CurrentLocale = expectedLocale
    }

    var wasOnOpenArchivePageBefore: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(StoryArchiveHolder.storyArchive) {
        if (StoryArchiveHolder.isOpen) {
            Router.goTo(StoriesPage())
        } else {
            Router.goTo(OpenArchivePage(onAppStart = !wasOnOpenArchivePageBefore))
            wasOnOpenArchivePageBefore = true
        }
    }

    LocalThemeProvider {
        UpdateChecker()
        AppContent()
    }
}

@Composable
private fun AppContent() {
    MaterialTheme(
        colors = when (LocalTheme.current) {
            Theme.LIGHT -> LightColors
            Theme.DARK -> DarkColors
        }
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides when (LocalTheme.current) {
                Theme.LIGHT -> LightExtendedColors
                Theme.DARK -> DarkExtendedColors
            }
        ) {
            Scaffold(
                topBar = { TopBar() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Router.CurrentPageBody()
                }
            }
        }
    }
}

@Composable
private fun UpdateChecker() {
    var notifyAboutNewUpdate: NewUpdate? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        if (settings.checkUpdates) {
            val newUpdate = AppVersionUtils.checkForUpdates()
            if (newUpdate != null) {
                if (newUpdate.version != settings.ignoreVersion) {
                    Napier.i { "Notifying about new update: ${newUpdate.version}" }
                    notifyAboutNewUpdate = newUpdate
                } else {
                    Napier.i { "Skipping notification about new update: ${newUpdate.version}" }
                }
            } else {
                Napier.i { "No new updates" }
            }
        }
    }

    notifyAboutNewUpdate?.let { newUpdate ->
        val uriHandler = LocalUriHandler.current
        ConfirmationDialog(
            title = { Text(Strings.updateCheck_newUpdate.remember()) },
            dismissButtonText = Strings.updateCheck_skipButton.remember(),
            onDismissRequest = {
                AppSettingsHolder.update(settings.copy(ignoreVersion = newUpdate.version))
                notifyAboutNewUpdate = null
                Napier.i { "User skipped new update ${newUpdate.version}" }
            },
            confirmButtonText = Strings.updateCheck_okButton.remember(),
            onConfirmRequest = {
                notifyAboutNewUpdate = null
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(Strings.updateCheck_message_newUpdate.remember())
                Text(newUpdate.version, fontWeight = FontWeight.Bold)
                Text(Strings.updateCheck_message_releasedOn.remember())
                Date(newUpdate.date)
            }
            Text(Strings.updateCheck_message_noAutoUpdate.remember())
            TextButton(onClick = { uriHandler.openUri(newUpdate.url) }) {
                Text(newUpdate.url)
            }
        }
    }
}