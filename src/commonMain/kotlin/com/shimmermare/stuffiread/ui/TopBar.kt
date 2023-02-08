package com.shimmermare.stuffiread.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.pages.stories.StoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router

@Composable
fun TopBar(router: Router, onResetAppStateRequest: () -> Unit) {
    var menuOpened: Boolean by remember { mutableStateOf(false) }

    TopAppBar(
        title = { router.CurrentPageTitle() },
        navigationIcon = {
            Box {
                IconButton(onClick = { menuOpened = true }) {
                    Icon(Icons.Filled.Menu, null)
                }
                Menu(menuOpened, onDismissRequest = { menuOpened = false }, onResetAppStateRequest)
            }
        },
        actions = {
            GoToPageActionButton(router, StoriesPage) { EmptyData }
            GoToPageActionButton(router, TagsPage) { EmptyData }
            GoToPageActionButton(router, TagCategoriesPage) { EmptyData }
        }
    )
}

@Composable
private inline fun <D : PageData> GoToPageActionButton(router: Router, page: Page<D>, crossinline data: () -> D) {
    ActionButton(page.name, router.currentPage == page, onClick = { router.goTo(page, data.invoke()) })
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