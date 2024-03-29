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
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.components.search.SearchList
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
import de.comahe.i18n4k.strings.LocalizedStringFactory1

/**
 * Base for page with mutable table of items.
 */
abstract class MutableTablePage<Id, Item> : LoadedPage<Map<Id, Item>>() {
    @Composable
    override fun LoadedContent() {
        // SnapshotStateMap is either bugged or PITA to use, immutability FTW
        var itemsById: Map<Id, Item> by remember(this, content) { mutableStateOf(content!!) }

        var showDeleteDialogFor: Item? by remember(this) { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { Router.goToCreatePage() }) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        ) {
            SearchList(
                itemsById.values,
                nameGetter = { it.name() },
                resultText = getSearchResultText(),
            ) { filtered ->
                TableContent(filtered, onDeleteRequest = { showDeleteDialogFor = it })
            }
        }

        if (showDeleteDialogFor != null) {
            DeleteDialog(
                showDeleteDialogFor!!,
                onDeleted = {
                    itemsById = itemsById - showDeleteDialogFor!!.id()
                    showDeleteDialogFor = null
                },
                onDismissRequest = { showDeleteDialogFor = null },
            )
        }
    }

    protected abstract fun Item.id(): Id

    protected abstract fun Item.name(): String

    protected abstract fun getSearchResultText(): PluralLocalizedString<LocalizedStringFactory1>

    protected abstract fun Router.goToCreatePage()

    @Composable
    protected abstract fun TableContent(
        items: Collection<Item>,
        onDeleteRequest: (Item) -> Unit
    )

    @Composable
    protected abstract fun DeleteDialog(item: Item, onDeleted: () -> Unit, onDismissRequest: () -> Unit)
}