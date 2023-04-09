package com.shimmermare.stuffiread.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
import de.comahe.i18n4k.strings.LocalizedStringFactory1

/**
 * List with search through named items and customizable advanced search.
 *
 * @param resultText - provider of localized text lines with amount of found units.
 *     E.g. 1 -> "found 1 item", 2 -> "found 2 items" and so on.
 */
@Composable
fun <T> SearchList(
    items: Collection<T>,
    modifier: Modifier = Modifier.padding(20.dp),
    nameGetter: (T) -> String,
    resultText: PluralLocalizedString<LocalizedStringFactory1>,
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
            modifier = Modifier.widthIn(min = 200.dp, max = 600.dp)
        ) {
            SearchBar(
                searchText = searchByNameText,
                placeholderText = Strings.components_searchList_searchBarPlaceholder.remember(),
                onSearchTextChanged = { searchByNameText = it },
            )
        }

        Text(
            text = resultText.remember(filteredItems.size),
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
    if (searchFor.isBlank()) return this
    val searchForTrimmed = searchFor.trim()
    return filter { nameGetter(it).contains(searchForTrimmed, ignoreCase = true) }
}