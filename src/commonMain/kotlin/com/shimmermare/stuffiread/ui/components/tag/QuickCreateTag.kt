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
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.util.remember

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
            Text(Strings.components_quickCreateTag_title.remember(), style = MaterialTheme.typography.h6)
            TagForm(
                creationMode = true,
                tag = Tag(
                    name = TagName(Strings.components_tagForm_name_default_new()),
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