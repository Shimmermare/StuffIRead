package com.shimmermare.stuffiread.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * List with search through named items and customizable advanced search.
 */
@Composable
fun <T> SearchList(
    items: Collection<T>,
    modifier: Modifier = Modifier.padding(20.dp),
    nameGetter: (T) -> String,
    unitNameProvider: (Int) -> String = { if (it == 1) "item" else "items" },
    table: @Composable (List<T>) -> Unit
) {
    var searchByNameText: String by remember { mutableStateOf("") }
    val filteredItems: List<T> = remember(items, searchByNameText) {
        items.asSequence().filterByName(searchByNameText, nameGetter).toList()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.33F)
        ) {
            SearchBar(
                searchText = searchByNameText,
                placeholderText = "Search by name",
                onSearchTextChanged = { searchByNameText = it },
                onClearClick = { searchByNameText = "" },
            )
        }

        Text(
            text = when (filteredItems.size) {
                0 -> "No ${unitNameProvider(0)} found"
                else -> "${filteredItems.size} ${unitNameProvider(filteredItems.size)} found"
            },
            style = MaterialTheme.typography.h6
        )

        Divider(modifier = Modifier.fillMaxWidth())

        table(filteredItems)
    }
}

private fun <T> Sequence<T>.filterByName(
    searchFor: String,
    nameGetter: (T) -> String,
): Sequence<T> {
    if (searchFor.isEmpty()) return this
    val searchForPrepared = searchFor.lowercase().trim()

    return filter { nameGetter(it).lowercase().contains(searchForPrepared.lowercase()) }
}