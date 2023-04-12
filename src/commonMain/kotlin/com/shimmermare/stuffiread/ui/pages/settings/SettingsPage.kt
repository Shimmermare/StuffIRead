package com.shimmermare.stuffiread.ui.pages.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.settings.ScoreDisplayType
import com.shimmermare.stuffiread.settings.ThemeBehavior
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.ui.AppSettingsHolder
import com.shimmermare.stuffiread.ui.CurrentLocale
import com.shimmermare.stuffiread.ui.components.form.EnumFormField
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.LeanBoolFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.input.OutlinedDropdownField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.story.StoryScore
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.util.remember
import de.comahe.i18n4k.getDisplayNameInLocale
import de.comahe.i18n4k.i18n4k

class SettingsPage : Page {
    @Composable
    override fun Body() {
        VerticalScrollColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SettingsForm()
            }
        }
    }

    @Composable
    private fun SettingsForm() {
        val resetButtonText = Strings.page_settings_resetButton.remember()
        SubmittableInputForm(
            data = AppSettingsHolder.settings,
            modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
            submitButtonText = Strings.page_settings_saveButton.remember(),
            onSubmit = {
                AppSettingsHolder.update(it)
            },
            actions = { state ->
                Button(
                    onClick = {
                        AppSettingsHolder.reset()
                    },
                    enabled = state.data != state.data.copyAndResetUserSettings()
                ) {
                    Text(resetButtonText)
                }
            },
        ) { state ->
            Text(Strings.page_settings_section_app.remember(), style = MaterialTheme.typography.h5)
            LocaleField(state)
            OpenLastArchiveOnStartupField(state)
            CheckUpdatesField(state)
            Text(Strings.page_settings_section_style.remember(), style = MaterialTheme.typography.h5)
            ThemeField(state)
            ScoreDisplayField(state)
            Text(Strings.page_settings_section_import.remember(), style = MaterialTheme.typography.h5)
            ShowForeignSourcesField(state)
            PonyIntegrationsField(state)

        }
    }

    @Composable
    private fun LocaleField(state: InputFormState<AppSettings>) {
        val description = remember {
            val defaultLocaleSupported = Strings.locales.contains(i18n4k.defaultLocale)
            if (defaultLocaleSupported) {
                null
            } else {
                "Warning: system locale (${i18n4k.defaultLocale.getDisplayNameInLocale()}) " +
                        "is not supported by application. English will be used instead."
            }
        }
        FormField(
            id = "locale",
            state = state,
            name = Strings.page_settings_locale.remember(),
            description = description,
            getter = { it.locale },
            setter = { form, value -> form.copy(locale = value) },
        ) { value, _, onValueChange ->
            val defaultValueText = Strings.page_settings_locale_default.remember()
            val choices = remember(defaultValueText) {
                buildMap {
                    put(null, defaultValueText)
                    Strings.locales.forEach { put(it, it.getDisplayNameInLocale()) }
                }
            }
            val choicesSet = remember(choices) { choices.toList().toSet() }
            OutlinedDropdownField(
                value = value to choices[value].toString(),
                onValueChange = { onValueChange(it.first) },
                displayNameProvider = { it.second },
                allowedValues = choicesSet,
            )
        }
    }

    @Composable
    private fun CheckUpdatesField(state: InputFormState<AppSettings>) {
        LeanBoolFormField(
            id = "checkUpdates",
            state = state,
            name = Strings.page_settings_checkUpdates.remember(),
            getter = { it.checkUpdates },
            setter = { form, value -> form.copy(checkUpdates = value) },
        )
    }

    @Composable
    private fun ThemeField(state: InputFormState<AppSettings>) {
        val options = remember(CurrentLocale) {
            mapOf(
                ThemeBehavior.USE_SYSTEM to Strings.page_settings_theme_default.toString(),
                ThemeBehavior.FORCE_LIGHT to Strings.page_settings_theme_light.toString(),
                ThemeBehavior.FORCE_DARK to Strings.page_settings_theme_dark.toString(),
            )
        }
        EnumFormField(
            id = "theme",
            state = state,
            name = Strings.page_settings_theme.remember(),
            allowedValues = ThemeBehavior.values,
            getter = { it.themeBehavior },
            setter = { form, value -> form.copy(themeBehavior = value) },
            displayNameProvider = { options.getOrDefault(it, it.name) }
        )
    }

    @Composable
    private fun ScoreDisplayField(state: InputFormState<AppSettings>) {
        val options = remember(CurrentLocale) {
            mapOf(
                ScoreDisplayType.STARS_5 to Strings.page_settings_scoreDisplay_5stars.toString(),
                ScoreDisplayType.STARS_10 to Strings.page_settings_scoreDisplay_10stars.toString(),
                ScoreDisplayType.NUMBERS_1_TO_10 to Strings.page_settings_scoreDisplay_numbers1to10.toString(),
                ScoreDisplayType.NUMBERS_1_TO_100 to Strings.page_settings_scoreDisplay_numbers1to100.toString(),
            )
        }
        EnumFormField(
            id = "scoreDisplay",
            state = state,
            name = Strings.page_settings_scoreDisplay.remember(),
            allowedValues = ScoreDisplayType.values,
            getter = { it.scoreDisplayType },
            setter = { form, value -> form.copy(scoreDisplayType = value) },
            displayNameProvider = { options.getOrDefault(it, it.name) }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(Strings.page_settings_scoreDisplay_example.remember())
            StoryScore(state.data.scoreDisplayType, Score(0.49F))
        }
    }

    @Composable
    private fun OpenLastArchiveOnStartupField(state: InputFormState<AppSettings>) {
        LeanBoolFormField(
            id = "openLastArchiveOnStartup",
            state = state,
            name = Strings.page_settings_openLastArchiveOnStartup.remember(),
            getter = { it.openLastArchiveOnStartup },
            setter = { form, value -> form.copy(openLastArchiveOnStartup = value) },
        )
    }

    @Composable
    private fun PonyIntegrationsField(state: InputFormState<AppSettings>) {
        LeanBoolFormField(
            id = "ponyIntegrations",
            state = state,
            name = Strings.page_settings_ponyIntegrations.remember(),
            getter = { it.enablePonyIntegrations },
            setter = { form, value -> form.copy(enablePonyIntegrations = value) },
        )
    }

    @Composable
    private fun ShowForeignSourcesField(state: InputFormState<AppSettings>) {
        LeanBoolFormField(
            id = "showForeignImportSources",
            state = state,
            name = Strings.page_settings_showForeignImportSources.remember(),
            getter = { it.showForeignImportSources },
            setter = { form, value -> form.copy(showForeignImportSources = value) },
        )
    }
}
