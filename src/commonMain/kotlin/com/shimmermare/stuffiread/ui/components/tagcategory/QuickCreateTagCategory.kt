package com.shimmermare.stuffiread.ui.components.tagcategory

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
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.util.remember

@Composable
fun QuickCreateTagCategory(
    onCloseRequest: () -> Unit,
    onCreate: (TagCategory) -> Unit,
) {
    FullscreenPopup {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Text(Strings.components_quickCreateTagCategory_title.remember(), style = MaterialTheme.typography.h6)
            TagCategoryForm(
                creationMode = true,
                category = TagCategory(name = TagCategoryName(Strings.components_tagCategoryForm_name_default_new())),
                modifier = Modifier.width(800.dp),
                onBack = onCloseRequest,
                onSubmit = { onCreate(StoryArchiveHolder.tagService.createCategory(it)) }
            )
        }
    }
}