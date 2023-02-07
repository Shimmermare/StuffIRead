package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.ui.components.table.Table
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName
import com.shimmermare.stuffiread.ui.components.text.FilledNameText


@Composable
fun TagTable(
    tagCategories: List<TagCategory>,
    tags: List<Tag>,
    onClick: (Tag) -> Unit,
    onEdit: (Tag) -> Unit,
    onDelete: (Tag) -> Unit
) {
    val tagsById: Map<TagId, Tag> = remember(tags) { tags.associateBy { it.id } }
    val categoriesById: Map<TagCategoryId, TagCategory> = remember(tagCategories) {
        tagCategories.associateBy { it.id }
    }

    Table(
        items = tags,
        defaultOrder = Comparator.comparing<Tag, Int> { categoriesById[it.categoryId]?.sortOrder ?: 0 }
            .thenComparing(Tag::name)
            .thenComparing(Tag::id),
        onRowClick = onClick,
    ) {
        column(
            title = "Name",
            columnWeight = 1F,
            sorter = Comparator.comparing { it.name }
        ) { item ->
            TagName(item, categoriesById[item.categoryId]?.let { Color(it.color) })
        }

        column(
            title = "Category",
            columnWeight = 1F,
            sorter = Comparator.comparing { it.name }
        ) { item ->
            val category = categoriesById[item.categoryId]
            if (category == null) {
                FilledNameText("Category ${item.categoryId} is missing!", Color.Black)
            } else {
                TagCategoryName(category)
            }
        }

        column(
            title = "Description",
            columnWeight = 2F,
            sorter = Comparator.comparing { it.name }
        ) { item ->
            item.description?.let { Text(it) }
        }

        column(
            title = "Implied tags",
            columnWeight = 1F,
            sorter = Comparator.comparing { it.name }
        ) { item ->
            if (item.impliedTags.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val exampleImplied = item.impliedTags.first().let { tagsById[it] ?: error("Missing tag $it") }
                    TagName(exampleImplied, categoriesById[exampleImplied.categoryId]?.let { Color(it.color) })
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
                onEdit.invoke(item)
            })
        }
        column(
            header = { Icon(Icons.Filled.Delete, null, modifier = Modifier.alpha(0F)) },
        ) { item ->
            Icon(Icons.Filled.Delete, null, modifier = Modifier.clickable {
                onDelete.invoke(item)
            })
        }
    }
}
