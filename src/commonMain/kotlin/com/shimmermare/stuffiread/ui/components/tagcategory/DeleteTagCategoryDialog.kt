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
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.ui.tagService

/**
 * Dialog that presents to user option to safely delete tag category.
 *
 * If category has tags in it - user must provide replacement category before deleting current.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteTagCategoryDialog(
    category: TagCategory,
    onDeleted: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tagService = tagService

    val tagsInCategoryCount: UInt = remember(category.id) { tagService.getTagCountInCategory(category.id) }
    var replacementCategoryId: TagCategoryId by remember { mutableStateOf(TagCategoryId.None) }

    FixedAlertDialog(
        onDismissRequest = onDismiss,
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
            if (tagsInCategoryCount > 0u) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("$tagsInCategoryCount tags in this category. Before deleting category you must provide a replacement: ")
                    TagCategorySelector(
                        categoryId = replacementCategoryId,
                        filter = { it.id != category.id },
                        onSelected = { replacementCategoryId = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = tagsInCategoryCount == 0u || replacementCategoryId != TagCategoryId.None,
                onClick = {
                    if (tagsInCategoryCount > 0u) {
                        tagService.changeTagsCategory(category.id, replacementCategoryId)
                        if (tagService.getTagCountInCategory(category.id) > 0u) {
                            throw IllegalStateException("Failed to change category ${category.id} -> $replacementCategoryId")
                        }
                    }
                    tagService.deleteCategoryById(category.id)
                    onDeleted()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}