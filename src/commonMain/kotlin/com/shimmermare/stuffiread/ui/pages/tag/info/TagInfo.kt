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
import com.shimmermare.stuffiread.domain.tags.ExtendedTag
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.components.tag.TagName
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryName
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageData
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router

@Composable
fun TagInfo(router: Router, app: AppState, tag: ExtendedTag) {
    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { router.goTo(EditTagPage, EditTagPageData.createCopy(tag.tag)) }) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(onClick = { router.goTo(EditTagPage, EditTagPageData.edit(tag.tag)) }) {
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
                    PropertiesBlock(router, tag)
                }
                Box(
                    modifier = Modifier.weight(0.5F)
                ) {
                    StatsBlock()
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteTagDialog(
            tag = tag,
            onConfirm = {
                app.tagService.deleteById(tag.tag.id)
                showDeleteDialog = false
                router.goTo(TagsPage, EmptyData)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PropertiesBlock(router: Router, tag: ExtendedTag) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Name", style = MaterialTheme.typography.h6)
                TagName(router, tag)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Category", style = MaterialTheme.typography.h6)
                TagCategoryName(router, tag.category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.h6)
                if (tag.tag.description.isPresent) {
                    Text(text = tag.tag.description.value!!)
                } else {
                    Text(text = "No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                val text = if (tag.implyingTags.isEmpty()) {
                    "Not implied by other tags"
                } else {
                    "Implied by ${tag.implyingTags.size} tags"
                }
                Text(text = text, style = MaterialTheme.typography.h6)
                tag.implyingTags.forEach {
                    TagName(router, it)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                val text = if (tag.impliedTags.isEmpty()) {
                    "Not implies other tags"
                } else {
                    "Implies ${tag.impliedTags.size} tags"
                }
                Text(text = text, style = MaterialTheme.typography.h6)
                tag.impliedTags.forEach {
                    TagName(router, it)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Created", style = MaterialTheme.typography.h6)
                Date(tag.tag.created)
            }
            if (tag.tag.created != tag.tag.updated) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = "Updated", style = MaterialTheme.typography.h6)
                    Date(tag.tag.updated)
                }
            }
        }
    }
}

@Composable
private fun StatsBlock(
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Stories with tag: TODO", style = MaterialTheme.typography.h6)
    }
}

