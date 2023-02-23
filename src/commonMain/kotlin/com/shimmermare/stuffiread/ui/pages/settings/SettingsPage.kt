package com.shimmermare.stuffiread.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.settings.ScoreDisplayType
import com.shimmermare.stuffiread.settings.ThemeBehavior
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.form.EnumFormField
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.pages.LoadedPage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class SettingsPage : LoadedPage<AppSettings>() {
    override suspend fun load(app: AppState): AppSettings {
        return app.settingsService.getSettings()
    }

    @Composable
    override fun LoadingError(app: AppState) {
        val coroutineScope = rememberCoroutineScope()

        Napier.e(error) { "Failed to load settings" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load settings",
                exception = error,
                suggestion = "Reset to default settings? Backup will be created for existing settings.",
                actions = listOf(
                    ErrorPage.Action("Reset") {
                        coroutineScope.launch {
                            app.settingsService.resetSettings()
                            app.router.goTo(SettingsPage())
                        }
                    }
                )
            )
        )
    }

    @Composable
    override fun LoadedContent(app: AppState) {
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            InputForm(
                value = content!!,
                submitButtonText = "Save",
                onSubmit = {
                    coroutineScope.launch {
                        app.settingsService.updateSettings(it)
                        app.router.goTo(SettingsPage())
                    }
                },
                fields = listOf(
                    EnumFormField(
                        name = "Theme",
                        enumType = ThemeBehavior::class,
                        getter = { it.themeBehavior },
                        setter = { form, value -> form.copy(themeBehavior = value) },
                        displayNameProvider = {
                            when (it) {
                                ThemeBehavior.USE_SYSTEM -> "System default"
                                ThemeBehavior.FORCE_LIGHT -> "Light"
                                ThemeBehavior.FORCE_DARK -> "Dark"
                            }
                        }
                    ),
                    EnumFormField(
                        name = "Score display",
                        enumType = ScoreDisplayType::class,
                        getter = { it.scoreDisplayType },
                        setter = { form, value -> form.copy(scoreDisplayType = value) },
                        displayNameProvider = {
                            when (it) {
                                ScoreDisplayType.STARS_5 -> "5 Stars"
                                ScoreDisplayType.STARS_10 -> "10 Stars"
                                ScoreDisplayType.NUMBERS_1_TO_10 -> "Numbers 1/10"
                                ScoreDisplayType.NUMBERS_1_TO_100 -> "Numbers 1/100"
                            }
                        }
                    )
                )
            )
        }
    }
}