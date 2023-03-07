package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.table.Table
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
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
            title = "Implications",
            columnWeight = 1F,
        ) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagImplication(
                    items = item.impliedTags,
                    tooltip = "Implied tags",
                    indirect = false,
                    icon = Icons.Filled.ArrowDownward
                )
                TagImplication(
                    items = item.indirectlyImpliedTags,
                    tooltip = "Indirectly implied tags",
                    indirect = true,
                    icon = Icons.Filled.ArrowDownward
                )
                TagImplication(
                    items = item.implyingTags,
                    tooltip = "Implying tags",
                    indirect = false,
                    icon = Icons.Filled.ArrowUpward
                )
                TagImplication(
                    items = item.indirectlyImplyingTags,
                    tooltip = "Indirectly implying tags",
                    indirect = true,
                    icon = Icons.Filled.ArrowUpward
                )
            }
        }

        column(
            header = { Icon(Icons.Filled.Edit, null, modifier = Modifier.alpha(0F)) },
        ) { item ->
            Icon(Icons.Filled.Edit, null, modifier = Modifier.clickable { onEditRequest(item) })
        }
        column(
            header = { Icon(Icons.Filled.Delete, null, modifier = Modifier.alpha(0F)) },
        ) { item ->
            Icon(Icons.Filled.Delete, null, modifier = Modifier.clickable { onDeleteRequest(item) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TagImplication(
    items: List<TagWithCategory>,
    tooltip: String,
    indirect: Boolean,
    icon: ImageVector
) {
    if (items.isNotEmpty()) {
        TooltipArea(
            tooltip = {
                PopupContent {
                    Column(
                        modifier = Modifier.padding(10.dp).sizeIn(maxHeight = 400.dp, maxWidth = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(tooltip, style = MaterialTheme.typography.subtitle1)
                        ChipVerticalGrid {
                            items.forEach {
                                TagNameRoutable(it, indirect = indirect)
                            }
                        }
                    }
                }
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val alphaMultiplier = if (indirect) 0.6F else 1F
                Icon(
                    icon,
                    null,
                    tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current * alphaMultiplier)
                )
                Text(items.size.toString())
            }
        }
    }
}
