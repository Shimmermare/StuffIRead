package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.foundation.layout.*
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
import com.shimmermare.stuffiread.domain.tags.*
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageData
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router

@Composable
fun TagInfo(router: Router, app: AppState, tag: Tag) {
    val category: TagCategory? = remember(tag.categoryId) { app.tagCategoryService.getById(tag.categoryId) }

    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { router.goTo(EditTagPage, EditTagPageData.createCopy(tag)) }) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(onClick = { router.goTo(EditTagPage, EditTagPageData(tag)) }) {
                    Icon(Icons.Filled.Edit, null)
                }
                FloatingActionButton(onClick = { showDeleteDialog = true }) {
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
                    PropertiesBlock(router, app, tag, category)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock(app.tagService, app.tagCategoryService, tag, category)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTagDialog(app.tagCategoryService, app.tagService, tag) { deleted ->
            showDeleteDialog = false
            if (deleted) {
                router.goTo(TagsPage, EmptyData)
            }
        }
    }
}

@Composable
private fun PropertiesBlock(router: Router, app: AppState, tag: Tag, category: TagCategory?) {
    val implyingTags: List<Tag> = remember(tag.id) { app.tagService.getImplying(tag.id) }
    val impliedTags: List<Tag> = remember(tag.impliedTags) { app.tagService.getTags(tag.impliedTags) }
    val colorsByCategoryId: Map<TagCategoryId, Color> = remember(implyingTags, impliedTags) {
        app.tagCategoryService.getColorsByIds((implyingTags + impliedTags).map { it.categoryId }.toSet())
            .mapValues { Color(it.value) }
    }

    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Name", style = MaterialTheme.typography.h6)
                TagName(router, tag, category?.let { Color(it.color) })
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Category", style = MaterialTheme.typography.h6)
                if (category != null) {
                    TagCategoryName(router, category)
                } else {
                    FilledNameText("Category ${tag.categoryId} is missing!", Color.Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.h6)
                if (tag.description != null) {
                    Text(text = tag.description)
                } else {
                    Text(text = "No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                val text = if (implyingTags.isEmpty()) {
                    "Not implied by other tags"
                } else {
                    "Implied by ${implyingTags.size} tags"
                }
                Text(text = text, style = MaterialTheme.typography.h6)
                implyingTags.forEach {
                    TagName(router, it, colorsByCategoryId[it.categoryId])
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                val text = if (impliedTags.isEmpty()) {
                    "Not implies other tags"
                } else {
                    "Implies ${impliedTags.size} tags"
                }
                Text(text = text, style = MaterialTheme.typography.h6)
                impliedTags.forEach {
                    TagName(router, it, colorsByCategoryId[it.categoryId])
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(
    tagService: TagService,
    tagCategoryService: TagCategoryService,
    tag: Tag,
    category: TagCategory?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Stories with tag: TODO", style = MaterialTheme.typography.h6)
    }
}

