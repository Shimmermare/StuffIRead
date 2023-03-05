package com.shimmermare.stuffiread.ui.pages.tag.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.ui.components.date.Date
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.components.tag.TagNameRoutable
import com.shimmermare.stuffiread.ui.components.tagcategory.TagCategoryNameRoutable
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.router

@Composable
fun TagInfo(tag: ExtendedTag) {
    val router = router

    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { router.goTo(EditTagPage.createCopy(tag.tag)) }) {
                    Icon(Icons.Filled.ContentCopy, null)
                }
                FloatingActionButton(onClick = { router.goTo(EditTagPage.edit(tag.tag)) }) {
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
                    PropertiesBlock(tag)
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
            onDeleted = {
                showDeleteDialog = false
                router.goTo(TagsPage())
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun PropertiesBlock(tag: ExtendedTag) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Name", style = MaterialTheme.typography.h6)
                TagNameRoutable(tag)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Category", style = MaterialTheme.typography.h6)
                TagCategoryNameRoutable(tag.category)
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "Description", style = MaterialTheme.typography.h6)
                if (tag.tag.description.isPresent) {
                    Text(text = tag.tag.description.value!!)
                } else {
                    Text(text = "No description", fontStyle = FontStyle.Italic, color = Color.LightGray)
                }
            }
            if (tag.implyingTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Implied by ${tag.implyingTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.implyingTags.forEach {
                            TagNameRoutable(it)
                        }
                    }
                }
            }
            if (tag.indirectlyImplyingTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Indirectly implied by ${tag.indirectlyImplyingTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.indirectlyImplyingTags.forEach {
                            TagNameRoutable(it, indirect = true)
                        }
                    }
                }
            }
            if (tag.impliedTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Implies ${tag.impliedTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.impliedTags.forEach {
                            TagNameRoutable(it)
                        }
                    }
                }
            }
            if (tag.indirectlyImpliedTags.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Indirectly implies ${tag.indirectlyImpliedTags.size} tag(s)",
                        style = MaterialTheme.typography.h6
                    )
                    ChipVerticalGrid {
                        tag.indirectlyImpliedTags.forEach {
                            TagNameRoutable(it, indirect = true)
                        }
                    }
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

