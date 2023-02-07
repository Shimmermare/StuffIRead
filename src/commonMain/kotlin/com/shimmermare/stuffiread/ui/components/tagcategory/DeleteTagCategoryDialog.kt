package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagCategoryService
import com.shimmermare.stuffiread.domain.tags.TagService
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteTagCategoryDialog(
    tagCategoryService: TagCategoryService,
    tagService: TagService,
    category: TagCategory,
    onClose: (deleted: Boolean) -> Unit
) {
    val tagsInCategoryCount = remember(category.id) { tagService.getCountInCategory(category.id) }
    var replacementCategoryId: TagCategoryId? by remember { mutableStateOf(null) }

    FixedAlertDialog(
        onDismissRequest = { onClose(false) },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Confirm removal of ")
                TagCategoryName(category)
            }
        },
        text = {
            if (tagsInCategoryCount > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("$tagsInCategoryCount tags in this category. Before deleting category you must provide a replacement: ")
                    TagCategorySelector(
                        tagCategoryService = tagCategoryService,
                        categoryId = replacementCategoryId,
                        filter = { it.id != category.id },
                        onSelect = { replacementCategoryId = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = tagsInCategoryCount == 0 || replacementCategoryId != null,
                onClick = {
                    if (tagsInCategoryCount > 0) {
                        tagService.changeTagCategory(category.id, replacementCategoryId!!)
                        if (tagService.getCountInCategory(category.id) > 0) {
                            throw IllegalStateException("Failed to change category ${category.id} -> $replacementCategoryId")
                        }
                    }
                    tagCategoryService.deleteById(category.id)
                    onClose(true)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onClose(false) }) {
                Text("Cancel")
            }
        }
    )
}