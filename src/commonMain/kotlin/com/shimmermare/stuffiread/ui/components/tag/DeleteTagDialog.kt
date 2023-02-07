package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryService
import com.shimmermare.stuffiread.domain.tags.TagService
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteTagDialog(
    tagCategoryService: TagCategoryService,
    tagService: TagService,
    tag: Tag,
    onClose: (deleted: Boolean) -> Unit
) {
    val category: TagCategory? = remember(tag.categoryId) { tagCategoryService.getById(tag.categoryId) }

    FixedAlertDialog(
        onDismissRequest = { onClose(false) },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Confirm removal of ")
                TagName(tag, category?.let { Color(it.color) })
            }
        },
        text = {
            Column {
                Text("Tag is used by TODO stories")
                if (tag.impliedTags.isNotEmpty()) {
                    Text("${tag.impliedTags.size} tags are implied by it")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    tagService.deleteById(tag.id)
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