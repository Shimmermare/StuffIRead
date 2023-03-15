package com.shimmermare.stuffiread.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.util.windowSize

/**
 * Layout that looks like this:
 * [ CURRENT PICK ]
 * [ SEARCH BAR ]
 * [ AVAILABLE TO PICK ]
 * [ ACTION BUTTONS ]
 *
 * Search bar and action buttons won't shrink down if there's a lot of picked/available to pick items.
 */
@Composable
inline fun PickerWithSearchLayout(
    modifier: Modifier = Modifier
        .padding(10.dp)
        .width(600.dp)
        .heightIn(max = windowSize.height - 100.dp),
    title: String,
    crossinline pickedItems: @Composable () -> Unit,
    crossinline searchBar: @Composable () -> Unit,
    crossinline availableToPickItems: @Composable () -> Unit,
    crossinline actionButtons: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
        )
        VerticalScrollColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1F, false)
        ) {
            pickedItems()
        }
        Column {
            searchBar()
        }
        VerticalScrollColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1F, false)
        ) {
            availableToPickItems()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            actionButtons()
        }
    }
}