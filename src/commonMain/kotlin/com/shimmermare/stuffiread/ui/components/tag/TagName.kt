package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.pages.tags.TagInfoPage
import com.shimmermare.stuffiread.ui.util.dashedBorder

val DefaultTagNameHeight = 30.dp
val DefaultTagNameModifier = Modifier.height(DefaultTagNameHeight)

@Composable
fun TagNameRoutable(
    tag: ExtendedTag,
    indirect: Boolean = false,
    fontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
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
    modifier: Modifier = DefaultTagNameModifier,
) {
    val effectiveColor = color.let { if (indirect) it.copy(alpha = 0.75F) else it }
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = if (indirect) 0.3F else 0.15F)
    DisableSelection {
        Box(
            modifier = Modifier
                .background(effectiveColor, shape = RoundedCornerShape(5.dp))
                .let {
                    if (indirect) {
                        it.dashedBorder(2.dp, 5.dp, borderColor)
                    } else {
                        it.border(2.dp, borderColor, RoundedCornerShape(5.dp))
                    }
                }
                .then(modifier),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = tag.name.value,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                maxLines = 1,
                fontSize = fontSize,
                style = TextStyle.Default.copy(
                    color = Color.White,
                    shadow = Shadow(color = Color.Black, blurRadius = 0.5F)
                ),
                overflow = TextOverflow.Visible
            )
        }
    }
}