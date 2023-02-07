package com.shimmermare.stuffiread.ui.pages.tagcategory.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tagcategory.DeleteTagCategoryDialog
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName
import com.shimmermare.stuffiread.ui.pages.tagcategories.TagCategoriesPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.util.ColorUtils.blueInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.greenInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.redInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.toHexColor

@Composable
fun TagCategoryInfo(router: Router, app: AppState, category: TagCategory) {
    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        router.goTo(EditTagCategoryPage, EditTagCategoryPageData.createCopy(category))
                    }
                ) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(
                    onClick = {
                        router.goTo(EditTagCategoryPage, EditTagCategoryPageData(category))
                    }
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
                modifier = Modifier.width(800.dp)
            ) {
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    PropertiesBlock(router, category)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock(router, app, category)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTagCategoryDialog(app.tagCategoryService, app.tagService, category) { deleted ->
            showDeleteDialog = false
            if (deleted) {
                router.goTo(TagCategoriesPage, EmptyData)
            }
        }
    }
}

@Composable
private fun PropertiesBlock(router: Router, category: TagCategory) {
    val color = Color(category.color)

    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Name", style = MaterialTheme.typography.h6)
                TagCategoryName(router, category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.h6)
                if (category.description != null) {
                    Text(text = category.description)
                } else {
                    Text(text = "No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Sorting order", style = MaterialTheme.typography.h6)
                Text(text = category.sortOrder.toString())
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Color", style = MaterialTheme.typography.h6)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(48.dp).background(color, CircleShape))
                    Text("HEX: ${color.toHexColor()}")
                    Text("RGB: ${color.redInt}, ${color.greenInt}, ${color.blueInt}")
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(router: Router, app: AppState, category: TagCategory) {
    val tagsInCategoryAndImplied: List<Tag> = remember(category.id) {
        app.tagService.getInCategoryWithImplied(category.id)
    }
    val colorsByCategoryId: Map<TagCategoryId, Color> = remember(category.id) {
        tagsInCategoryAndImplied.map { it.categoryId }.toSet()
            .let { app.tagCategoryService.getColorsByIds(it) }
            .mapValues { Color(it.value) }
    }

    val tagsInCategory: List<Tag> = remember(category.id) {
        tagsInCategoryAndImplied.filter { it.categoryId == category.id }.sortedBy { it.name }
    }
    val tagsImplied: List<Tag> = remember(category.id) {
        tagsInCategoryAndImplied.filter { it.categoryId != category.id }
            .sortedWith(Comparator.comparing(Tag::categoryId).thenComparing(Tag::name))
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Tags in category: ${tagsInCategory.size}", style = MaterialTheme.typography.h6)
        ChipVerticalGrid {
            tagsInCategory.forEach { tag ->
                TagName(router, tag, colorsByCategoryId[tag.categoryId])
            }
        }

        Text(text = "Tags implied by tags in category: ${tagsImplied.size}", style = MaterialTheme.typography.h6)
        ChipVerticalGrid {
            tagsImplied.forEach { tag ->
                TagName(router, tag, colorsByCategoryId[tag.categoryId])
            }
        }
    }
}