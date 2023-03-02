package com.shimmermare.stuffiread.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchBar(
                searchText = searchByNameText,
                placeholderText = "Search by name",
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .height(36.dp)
                    .background(color = Color.LightGray, shape = RoundedCornerShape(5.dp)),
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