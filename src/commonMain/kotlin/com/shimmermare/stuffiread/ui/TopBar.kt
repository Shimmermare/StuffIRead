package com.shimmermare.stuffiread.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.settings.SettingsPage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.Page

@Composable
fun TopBar(app: AppState, onResetAppStateRequest: () -> Unit) {
    var menuOpened: Boolean by remember { mutableStateOf(false) }

    TopAppBar(
        title = { app.router.CurrentPageTitle() },
        navigationIcon = {
            Box {
                IconButton(onClick = { menuOpened = true }) {
                    Icon(Icons.Filled.Menu, null)
                }
                Menu(
                    app,
                    menuOpened,
                    onDismissRequest = { menuOpened = false },
                    onResetAppStateRequest = onResetAppStateRequest,
                    onOpenSettingsRequest = { app.router.goTo(SettingsPage()) },
                )
            }
        },
        actions = {
            if (app.storyArchive != null) {
                GoToPageActionButton(app, "Stories") { StoriesPage() }
                GoToPageActionButton(app, "Tags") { TagsPage() }
                GoToPageActionButton(app, "Tag categories") { TagCategoriesPage() }
            } else if (app.router.currentPage !is OpenArchivePage) {
                GoToPageActionButton(app, "Open archive") { OpenArchivePage() }
            }
        }
    )
}

@Composable
private inline fun <reified T : Page> GoToPageActionButton(
    app: AppState,
    pageName: String,
    crossinline pageSupplier: () -> T
) {
    ActionButton(pageName, app.router.currentPage is T, onClick = { app.router.goTo(pageSupplier()) })
}

@Composable
private fun ActionButton(text: String, highlighted: Boolean = false, onClick: () -> Unit) {
    val colors = if (highlighted) {
        ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
    } else {
        ButtonDefaults.buttonColors()
    }
    Button(
        modifier = Modifier.fillMaxHeight(),
        colors = colors,
        onClick = onClick
    ) {
        Text(
            style = MaterialTheme.typography.h6,
            text = text
        )
    }
}