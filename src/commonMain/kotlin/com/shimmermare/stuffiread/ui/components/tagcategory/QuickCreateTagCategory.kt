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
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.TagCategoryForm

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
            Text("Create tag category", style = MaterialTheme.typography.h6)
            TagCategoryForm(
                mode = EditTagCategoryPageMode.CREATE,
                category = TagCategory(name = TagCategoryName("New category")),
                modifier = Modifier.width(800.dp),
                onBack = onCloseRequest,
                onSubmit = { onCreate(StoryArchiveHolder.tagService.createCategory(it)) }
            )
        }
    }
}