package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode
import com.shimmermare.stuffiread.ui.pages.tag.edit.TagForm

@Composable
fun QuickCreateTag(
    onCloseRequest: () -> Unit,
    onCreate: (Tag) -> Unit,
) {
    FullscreenPopup {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(20.dp),
        ) {
            Text("Create tag", style = MaterialTheme.typography.h6)
            TagForm(
                mode = EditTagPageMode.CREATE,
                tag = Tag(
                    name = TagName("New tag"),
                    categoryId = TagCategoryId.None,
                ),
                modifier = Modifier.width(800.dp),
                onBack = onCloseRequest,
                onSubmit = {
                    val created = tagService.createTag(it)
                    onCreate(created)
                }
            )
        }
    }
}