package com.shimmermare.stuffiread.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.text.TextURI
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.AppVersionUtils

private const val GITHUB_URL = "https://github.com/Shimmermare/StuffIRead"

@Composable
fun AboutApp(onDismissRequest: () -> Unit) {
    val version: String = remember { AppVersionUtils.CURRENT_VERSION ?: "unknown" }
    FullscreenPopup {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.width(500.dp).padding(20.dp)
        ) {
            Text("Stuff I Read", style = MaterialTheme.typography.h5)

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(Strings.aboutApp_madeBy.remember())
                    TextURI("https://shimmermare.com", "Shimmermare")
                }
                Text(Strings.aboutApp_version.remember(version))

                Text(Strings.aboutApp_description.remember())
                TextURI(GITHUB_URL, style = MaterialTheme.typography.subtitle1)
            }

            Button(onClick = onDismissRequest) {
                Text(Strings.components_dialog_info_dismissButtonDefault.remember())
            }
        }
    }
}