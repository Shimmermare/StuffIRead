package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteTagDialog(
    tag: ExtendedTag,
    onDeleted: () -> Unit,
    onDismiss: () -> Unit,
) {
    FixedAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Confirm removal of ")
                TagName(tag)
            }
        },
        text = {
            Column {
                // TODO: Get number of stories used by tag
                if (true) {
                    Text("Tag is used by TODO stories")
                }
                if (tag.implyingTags.isNotEmpty()) {
                    Column {
                        Text("Tag is implied by ${tag.implyingTags.size} other tag(s)")
                        ChipVerticalGrid {
                            tag.implyingTags.forEach {
                                TagName(it)
                            }
                        }
                    }
                }
                if (tag.impliedTags.isNotEmpty()) {
                    Column {
                        Text("Tag implies ${tag.impliedTags.size} other tag(s)")
                        ChipVerticalGrid {
                            tag.impliedTags.forEach {
                                TagName(it)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tag.implyingTags.isNotEmpty()) {
                        // Remove direct implications
                        val toUpdate = tag.implyingTags.filter { it.tag.impliedTagIds.contains(tag.tag.id) }.map {
                            it.tag.copy(impliedTagIds = it.tag.impliedTagIds - tag.tag.id)
                        }
                        tagService.updateTags(toUpdate)
                    }
                    tagService.deleteTagById(tag.tag.id)
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