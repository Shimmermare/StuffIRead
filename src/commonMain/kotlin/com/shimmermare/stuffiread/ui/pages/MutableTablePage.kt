package com.shimmermare.stuffiread.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.search.SearchList
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Base for page with mutable table of items.
 */
abstract class MutableTablePage<Id, Item> : LoadedPage<Map<Id, Item>>() {
    @Composable
    override fun LoadedContent(app: AppState) {
        // SnapshotStateMap is either bugged or PITA to use, immutability FTW
        var itemsById: Map<Id, Item> by remember(this, content) { mutableStateOf(content!!) }

        var showDeleteDialogFor: Item? by remember(this, itemsById) { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { app.router.goToCreatePage() }) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        ) {
            SearchList(
                itemsById.values,
                nameGetter = { it.name() },
                unitNameProvider = ::getUnitName,
            ) { filtered ->
                TableContent(app, filtered, onDeleteRequest = { showDeleteDialogFor = it })
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
        app: AppState,
        items: Collection<Item>,
        onDeleteRequest: (Item) -> Unit
    )

    @Composable
    protected abstract fun DeleteDialog(app: AppState, item: Item, onDeleted: () -> Unit, onDismiss: () -> Unit)
}