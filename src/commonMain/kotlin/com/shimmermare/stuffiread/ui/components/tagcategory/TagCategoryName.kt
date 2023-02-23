package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Go to tag category page on click
 */
@Composable
fun TagCategoryName(
    router: Router,
    category: TagCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
) {
    TagCategoryName(
        category = category,
        fontSize = fontSize,
        height = height,
        onClick = { router.goTo(TagCategoryInfoPage(category.id)) }
    )
}

@Composable
fun TagCategoryName(
    category: TagCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    onClick: () -> Unit
) {
    TagCategoryName(
        category = category,
        fontSize = fontSize,
        height = height,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagCategoryName(
    category: TagCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    modifier: Modifier? = null,
) {
    FilledNameText(
        text = category.name.value,
        color = Color(category.color),
        fontSize = fontSize,
        height = height,
        modifier = modifier
    )
}