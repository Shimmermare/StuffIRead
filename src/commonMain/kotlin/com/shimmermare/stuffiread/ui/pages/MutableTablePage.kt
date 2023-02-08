package com.shimmermare.stuffiread.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.search.SearchList
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Base for page with mutable table of items.
 */
abstract class MutableTablePage<Data : PageData, Id, Item> : LoadedPage<Data, Map<Id, Item>>() {
    @Composable
    override fun LoadedContent(router: Router, app: AppState, loaded: Map<Id, Item>) {
        // SnapshotStateMap is either bugged or PITA to use, immutability FTW
        var itemsById: Map<Id, Item> by remember { mutableStateOf(loaded) }

        var showDeleteDialogFor: Item? by remember { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { router.goToCreatePage() }) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        ) {
            SearchList(
                itemsById.values,
                nameGetter = { it.name() },
                unitNameProvider = ::getUnitName,
            ) { filtered ->
                TableContent(router, app, filtered, onDeleteRequest = { showDeleteDialogFor = it })
            }
        }

        if (showDeleteDialogFor != null) {
            DeleteDialog(
                app,
                showDeleteDialogFor!!,
                onDeleted = {
                    itemsById = itemsById - showDeleteDialogFor!!.id()
                    showDeleteDialogFor = null
                },
                onDismiss = {
                    showDeleteDialogFor = null
                }
            )
        }
    }

    protected abstract fun Item.id(): Id

    protected abstract fun Item.name(): String

    protected abstract fun getUnitName(count: Int): String

    protected abstract fun Router.goToCreatePage()

    @Composable
    protected abstract fun TableContent(
        router: Router,
        app: AppState,
        items: Collection<Item>,
        onDeleteRequest: (Item) -> Unit
    )

    @Composable
    protected abstract fun DeleteDialog(app: AppState, item: Item, onDeleted: () -> Unit, onDismiss: () -> Unit)
}