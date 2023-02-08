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
import com.shimmermare.stuffiread.domain.tags.*
import com.shimmermare.stuffiread.ui.components.dialog.FixedAlertDialog
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteTagDialog(
    tag: ExtendedTag,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
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
                    if (tag.impliedTags.isNotEmpty()) {
                        Text("${tag.impliedTags.size} tags are implied by it")
                    }
                }
                if (tag.impliedTags.isNotEmpty()) {
                    Column {
                        Text("Tag implies ${tag.impliedTags.size} other tags")
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
                onClick = onConfirm
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