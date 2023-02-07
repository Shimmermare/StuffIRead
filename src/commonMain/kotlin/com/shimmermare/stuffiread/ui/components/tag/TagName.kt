package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPageData
import com.shimmermare.stuffiread.ui.routing.Router

@Composable
fun TagName(
    router: Router,
    tag: Tag,
    color: Color? = null,
    fontSize: TextUnit? = null,
    height: Dp? = null,
) {
    val modifier = Modifier.clickable { router.goTo(TagInfoPage, TagInfoPageData(tag)) }
    FilledNameText(
        text = tag.name,
        color = color ?: Color.Black,
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
    onClick: () -> Unit
) {
    FilledNameText(
        text = tag.name,
        color = color ?: Color.Black,
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
    modifier: Modifier? = null,
) {
    FilledNameText(
        text = tag.name,
        color = color ?: Color.Black,
        fontSize = fontSize,
        height = height,
        modifier = modifier
    )
}