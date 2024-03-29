package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.dialog.ConfirmationDialog
import com.shimmermare.stuffiread.ui.util.remember

/**
 * Dialog that presents to user option to safely delete tag category.
 *
 * If category has tags in it - user must provide replacement category before deleting current.
 */
@Composable
fun DeleteTagCategoryDialog(
    category: TagCategory,
    onDeleted: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val tagsInCategoryCount: UInt = remember(category.id) { tagService.getTagCountInCategory(category.id) }
    var replacementCategoryId: TagCategoryId by remember { mutableStateOf(TagCategoryId.None) }

    ConfirmationDialog(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(Strings.components_deleteTagCategoryDialog_title.remember())
                TagCategoryName(category)
            }
        },
        confirmButtonEnabled = tagsInCategoryCount == 0u || replacementCategoryId != TagCategoryId.None,
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
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
        if (tagsInCategoryCount > 0u) {
            Text(Strings.components_deleteTagCategoryDialog_replacement_description.remember(tagsInCategoryCount))
            TagCategoryPicker(
                title = Strings.components_deleteTagCategoryDialog_replacement_title.remember(),
                pickedCategoryId = replacementCategoryId,
                filter = { it.id != category.id },
                onPick = { replacementCategoryId = it }
            )
        }
    }
}