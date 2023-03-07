package com.shimmermare.stuffiread.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.pages.openarchive.OpenArchivePage
import com.shimmermare.stuffiread.ui.pages.settings.SettingsPage
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import com.shimmermare.stuffiread.ui.theme.Theme

@Composable
fun TopBar() {
    var menuOpened: Boolean by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Router.CurrentPageTitle() },
        navigationIcon = {
            Box {
                IconButton(onClick = { menuOpened = true }) {
                    Icon(Icons.Filled.Menu, null)
                }
                AppMenu(
                    menuOpened,
                    onDismissRequest = { menuOpened = false },
                    onOpenSettingsRequest = { Router.goTo(SettingsPage()) },
                )
            }
        },
        actions = {
            if (StoryArchiveHolder.isOpen) {
                GoToPageActionButton("Stories") { StoriesPage() }
                GoToPageActionButton("Tags") { TagsPage() }
                GoToPageActionButton("Tag categories") { TagCategoriesPage() }
            } else if (Router.currentPage !is OpenArchivePage) {
                GoToPageActionButton("Open archive") { OpenArchivePage() }
            }
        }
    )
}

@Composable
private inline fun <reified T : Page> GoToPageActionButton(
    pageName: String,
    crossinline pageSupplier: () -> T
) {
    ActionButton(pageName, Router.currentPage !is T, onClick = { Router.goTo(pageSupplier()) })
}

@Composable
private fun ActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val materialColors = MaterialTheme.colors
    val currentTheme = LocalTheme.current

    val backgroundColor: Color = remember(currentTheme, enabled) {
        when {
            enabled -> Color.Unspecified
            currentTheme == Theme.LIGHT -> materialColors.primaryVariant.copy(alpha = 0.75F)
            currentTheme == Theme.DARK -> materialColors.primary.copy(alpha = 0.5F)
            else -> Color.Unspecified
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = backgroundColor)
            .let {
                if (enabled) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(10.dp),
        )
    }
}