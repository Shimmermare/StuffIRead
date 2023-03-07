package com.shimmermare.stuffiread.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.settings.AppSettings
import com.shimmermare.stuffiread.settings.ScoreDisplayType
import com.shimmermare.stuffiread.settings.ThemeBehavior
import com.shimmermare.stuffiread.ui.AppSettingsHolder
import com.shimmermare.stuffiread.ui.components.form.BoolFormField
import com.shimmermare.stuffiread.ui.components.form.EnumFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.routing.Page

class SettingsPage : Page {
    @Composable
    override fun Body() {
        Column(
            modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SubmittableInputForm(
                data = AppSettingsHolder.settings,
                modifier = Modifier.padding(20.dp).sizeIn(maxWidth = 800.dp),
                submitButtonText = "Save",
                onSubmit = {
                    AppSettingsHolder.update(it)
                },
                actions = { state ->
                    Button(
                        onClick = {
                            AppSettingsHolder.reset()
                        },
                        enabled = state.data != AppSettings()
                    ) {
                        Text("Reset to default")
                    }
                },
            ) { state ->
                EnumFormField(
                    id = "theme",
                    state = state,
                    name = "Theme",
                    allowedValues = ThemeBehavior.values,
                    getter = { it.themeBehavior },
                    setter = { form, value -> form.copy(themeBehavior = value) },
                    displayNameProvider = {
                        when (it) {
                            ThemeBehavior.USE_SYSTEM -> "System default"
                            ThemeBehavior.FORCE_LIGHT -> "Light"
                            ThemeBehavior.FORCE_DARK -> "Dark"
                        }
                    }
                )
                EnumFormField(
                    id = "scoreDisplay",
                    state = state,
                    name = "Score display",
                    allowedValues = ScoreDisplayType.values,
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
                BoolFormField(
                    id = "openLastArchiveOnStartup",
                    state = state,
                    name = "On startup open last previously opened archive",
                    getter = { it.openLastArchiveOnStartup },
                    setter = { form, value -> form.copy(openLastArchiveOnStartup = value) },
                )
                BoolFormField(
                    id = "ponyIntegrations",
                    state = state,
                    name = "Enable pony website integrations",
                    getter = { it.enablePonyIntegrations },
                    setter = { form, value -> form.copy(enablePonyIntegrations = value) },
                )
            }
        }
    }
}