package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.pages.tagcategories.EditTagCategoryPage
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.util.ColorUtils.blueInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.greenInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.redInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.toHexColor
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun TagCategoryInfo(category: TagCategory) {

    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = { Router.goTo(EditTagCategoryPage.createCopy(category)) }
                ) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(
                    onClick = { Router.goTo(EditTagCategoryPage.edit(category)) }
                ) {
                    Icon(Icons.Filled.Edit, null)
                }
                FloatingActionButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(Icons.Filled.Delete, null)
                }
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
        ) {
            Row(
                modifier = Modifier.widthIn(min = 800.dp, max = 1200.dp)
            ) {
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    PropertiesBlock(category)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock(category)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTagCategoryDialog(
            category,
            onDeleted = {
                showDeleteDialog = false
                Router.goTo(TagCategoriesPage())
            },
            onDismissRequest = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun PropertiesBlock(category: TagCategory) {
    val color = Color(category.color)

    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagCategoryInfo_name.remember(), style = MaterialTheme.typography.h6)
                TagCategoryNameRoutable(category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagCategoryInfo_description.remember(), style = MaterialTheme.typography.h6)
                if (category.description.isPresent) {
                    Text(category.description.value!!)
                } else {
                    Text(
                        Strings.components_tagCategoryInfo_description_noDescription.remember(),
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagCategoryInfo_sortingOrder.remember(), style = MaterialTheme.typography.h6)
                Text(category.sortOrder.toString())
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagCategoryInfo_color.remember(), style = MaterialTheme.typography.h6)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(48.dp).background(color, CircleShape))
                    Text("HEX: ${color.toHexColor()}")
                    Text("RGB: ${color.redInt}, ${color.greenInt}, ${color.blueInt}")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(Strings.components_tagCategoryInfo_created.remember(), style = MaterialTheme.typography.h6)
                Date(category.created)
            }
            if (category.created != category.updated) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(Strings.components_tagCategoryInfo_updated.remember(), style = MaterialTheme.typography.h6)
                    Date(category.updated)
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(category: TagCategory) {

    val tagsInCategoryIncludingImplied: List<TagWithCategory> = remember(category.id) {
        tagService.getTagsInCategoryIncludingImplied(category.id)
    }
    val tagsInCategory: List<TagWithCategory> = remember(category.id) {
        tagsInCategoryIncludingImplied.filter { it.tag.categoryId == category.id }.sortedBy { it.tag.name }
    }
    val tagsImplied: List<TagWithCategory> = remember(category.id) {
        tagsInCategoryIncludingImplied.filter { it.tag.categoryId != category.id }.sortedBy { it.tag.name }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(Strings.components_tagCategoryInfo_tagsInCategory.remember(tagsInCategory.size), style = MaterialTheme.typography.h6)
        ChipVerticalGrid {
            tagsInCategory.forEach { tag ->
                TagNameRoutable(tag)
            }
        }

        Text(Strings.components_tagCategoryInfo_impliedTagsInCategory.remember(tagsImplied.size), style = MaterialTheme.typography.h6)
        ChipVerticalGrid {
            tagsImplied.forEach { tag ->
                TagNameRoutable(tag)
            }
        }
    }
}