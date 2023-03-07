package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage

@Composable
fun TagNameRoutable(
    tag: ExtendedTag,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    TagName(
        tag = tag,
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier,
        onClick = { Router.goTo(TagInfoPage(tag.tag.id)) }
    )
}

@Composable
fun TagNameRoutable(
    tag: TagWithCategory,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    TagName(
        tag = tag,
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier,
        onClick = { Router.goTo(TagInfoPage(tag.tag.id)) }
    )
}

@Composable
fun TagName(
    tag: ExtendedTag,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: TagWithCategory,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: Tag,
    color: Color,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        color = color,
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: ExtendedTag,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    TagName(
        tag = tag.tag,
        color = Color(tag.category.color),
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier
    )
}

@Composable
fun TagName(
    tag: TagWithCategory,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    TagName(
        tag = tag.tag,
        color = Color(tag.category.color),
        indirect = indirect,
        fontSize = fontSize,
        modifier = modifier
    )
}

@Composable
fun TagName(
    tag: Tag,
    color: Color,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = Modifier.height(30.dp),
) {
    FilledNameText(
        text = tag.name.value,
        color = color.let { if (indirect) it.copy(alpha = 0.6F) else it },
        fontSize = fontSize,
        modifier = modifier
    )
}