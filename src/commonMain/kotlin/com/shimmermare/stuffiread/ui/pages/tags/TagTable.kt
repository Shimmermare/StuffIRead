package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.ui.components.table.Table
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName


@Composable
fun TagTable(
    tags: Collection<ExtendedTag>,
    onRowClick: (ExtendedTag) -> Unit,
    onEditRequest: (ExtendedTag) -> Unit,
    onDeleteRequest: (ExtendedTag) -> Unit
) {
    Table(
        items = tags,
        defaultOrder = ExtendedTag.DEFAULT_ORDER,
        onRowClick = onRowClick,
    ) {
        column(
            title = "Name",
            columnWeight = 1F,
            sorter = Comparator.comparing { it.tag.name }
        ) { item ->
            TagName(item)
        }

        column(
            title = "Category",
            columnWeight = 1F,
            sorter = Comparator.comparing { it.category.name }
        ) { item ->
            TagCategoryName(item.category)
        }

        column(
            title = "Description",
            columnWeight = 2F,
            sorter = Comparator.comparing { it.tag.description }
        ) { item ->
            if (item.tag.description.isPresent) {
                Text(item.tag.description.value!!)
            }
        }

        column(
            title = "Implied tags",
            columnWeight = 1F,
        ) { item ->
            if (item.impliedTags.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val exampleImplied = item.impliedTags.first()
                    TagName(exampleImplied)
                    if (item.impliedTags.size > 1) {
                        Text(" and ${item.impliedTags.size - 1} more")
                    }
                }
            }
        }

        column(
            header = { Icon(Icons.Filled.Edit, null, modifier = Modifier.alpha(0F)) },
        ) { item ->
            Icon(Icons.Filled.Edit, null, modifier = Modifier.clickable {
                onEditRequest.invoke(item)
            })
        }
        column(
            header = { Icon(Icons.Filled.Delete, null, modifier = Modifier.alpha(0F)) },
        ) { item ->
            Icon(Icons.Filled.Delete, null, modifier = Modifier.clickable {
                onDeleteRequest.invoke(item)
            })
        }
    }
}
