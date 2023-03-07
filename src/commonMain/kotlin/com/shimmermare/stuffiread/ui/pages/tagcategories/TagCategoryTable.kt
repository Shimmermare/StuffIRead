package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.ui.components.table.Table
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName


@Composable
fun TagCategoryTable(
    categories: Collection<TagCategory>,
    onClickRequest: (TagCategory) -> Unit,
    onEditRequest: (TagCategory) -> Unit,
    onDeleteRequest: (TagCategory) -> Unit
) {
    // TODO: Should be done properly using measuring
    val indexColumnWidth: Dp = remember(categories) {
        val calculatedWidth = (categories.maxOfOrNull { it.sortOrder } ?: 0).toString().length * 12
        min(calculatedWidth.dp, 100.dp)
    }

    Table(
        items = categories,
        defaultOrder = TagCategory.DEFAULT_ORDER,
        onRowClick = onClickRequest,
    ) {
        column(
            header = {
                Row(
                    modifier = Modifier.width(indexColumnWidth)
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            },
            sorter = Comparator.comparing { it.sortOrder },
            hideSortIcon = true,
        ) { item ->
            Text(
                text = item.sortOrder.toString(),
                modifier = Modifier.width(indexColumnWidth),
                maxLines = 1,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Start,
            )
        }

        column(
            title = "Name",
            columnWeight = 0.33F,
            sorter = Comparator.comparing { it.name }
        ) { item ->
            TagCategoryName(item)
        }

        column(
            title = "Description",
            columnWeight = 0.67F,
            sorter = Comparator.comparing { it.description }
        ) { _, item ->
            item.description.value?.let { Text(it) }
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
