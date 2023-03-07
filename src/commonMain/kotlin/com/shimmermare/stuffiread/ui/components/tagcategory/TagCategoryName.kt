package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage

/**
 * Go to tag category page on click
 */
@Composable
fun TagCategoryNameRoutable(
    category: TagCategory,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    TagCategoryName(
        category = category,
        fontSize = fontSize,
        modifier = modifier,
        onClick = { Router.goTo(TagCategoryInfoPage(category.id)) }
    )
}

@Composable
fun TagCategoryName(
    category: TagCategory,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
    onClick: () -> Unit
) {
    TagCategoryName(
        category = category,
        fontSize = fontSize,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagCategoryName(
    category: TagCategory,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    FilledNameText(
        text = category.name.value,
        color = Color(category.color),
        fontSize = fontSize,
        modifier = modifier
    )
}