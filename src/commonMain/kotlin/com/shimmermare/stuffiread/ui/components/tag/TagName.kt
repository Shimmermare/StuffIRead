package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.routing.Router

@Composable
fun TagName(
    router: Router,
    tag: ExtendedTag,
    fontSize: TextUnit? = null,
    height: Dp? = null,
) {
    TagName(
        tag = tag,
        fontSize = fontSize,
        height = height,
        onClick = { router.goTo(TagInfoPage(tag.tag.id)) }
    )
}

@Composable
fun TagName(
    router: Router,
    tag: TagWithCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
) {
    TagName(
        tag = tag,
        fontSize = fontSize,
        height = height,
        onClick = { router.goTo(TagInfoPage(tag.tag.id)) }
    )
}

@Composable
fun TagName(
    router: Router,
    tag: Tag,
    color: Color? = null,
    fontSize: TextUnit? = null,
    height: Dp? = null,
) {
    TagName(
        tag = tag,
        color = color,
        fontSize = fontSize,
        height = height,
        onClick = { router.goTo(TagInfoPage(tag.id)) }
    )
}

@Composable
fun TagName(
    tag: ExtendedTag,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        fontSize = fontSize,
        height = height,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: TagWithCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        fontSize = fontSize,
        height = height,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: Tag,
    color: Color? = null,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    onClick: () -> Unit
) {
    TagName(
        tag = tag,
        color = color,
        fontSize = fontSize,
        height = height,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TagName(
    tag: ExtendedTag,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    modifier: Modifier? = null,
) {
    TagName(
        tag = tag.tag,
        color = Color(tag.category.color),
        fontSize = fontSize,
        height = height,
        modifier = modifier
    )
}

@Composable
fun TagName(
    tag: TagWithCategory,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    modifier: Modifier? = null,
) {
    TagName(
        tag = tag.tag,
        color = Color(tag.category.color),
        fontSize = fontSize,
        height = height,
        modifier = modifier
    )
}

@Composable
fun TagName(
    tag: Tag,
    color: Color? = null,
    fontSize: TextUnit? = null,
    height: Dp? = null,
    modifier: Modifier? = null,
) {
    FilledNameText(
        text = tag.name.value,
        color = color ?: Color.Black,
        fontSize = fontSize,
        height = height,
        modifier = modifier
    )
}