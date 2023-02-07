package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.domain.tags.*
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.searchbar.SearchBar

/**
 * Input to select multiple unique tags from list of all tags.
 *
 * @param filter additional filter for tags available for selection.
 */
@Composable
fun MultiTagSelector(
    tagCategoryService: TagCategoryService,
    tagService: TagService,
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onValueChange: (Set<TagId>) -> Unit
) {
    val selectedTags: List<Tag> = remember(selectedIds) { tagService.getTags(selectedIds) }
    val colorsByCategoryId: Map<TagCategoryId, Color> = remember(selectedTags) {
        tagCategoryService.getColorsByIds(selectedTags.map { it.categoryId })
            .mapValues { Color(it.value) }
    }

    var showPopup: Boolean by remember { mutableStateOf(false) }

    DisableSelection {
        if (showPopup) {
            Box {
                SelectorPopup(
                    tagCategoryService,
                    tagService,
                    selectedTags,
                    filter,
                    onDismissRequest = { showPopup = false },
                    onValueChange
                )
            }
        }
        ChipVerticalGrid {
            selectedTags.forEach {
                SelectedTagName(it, colorsByCategoryId) { onValueChange(selectedIds - it.id) }
            }

            Box(modifier = Modifier.clickable { showPopup = true }) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SelectedTagName(tag: Tag, colorsByCategoryId: Map<TagCategoryId, Color>, onUnselect: () -> Unit) {
    var pointerInside by remember(tag.id) { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) { pointerInside = true }
            .onPointerEvent(PointerEventType.Exit) { pointerInside = false },
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TagName(tag, colorsByCategoryId[tag.categoryId])
        if (pointerInside) {
            Box(modifier = Modifier.clickable(onClick = onUnselect)) {
                Icon(Icons.Filled.Clear, null, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
private fun SelectorPopup(
    tagCategoryService: TagCategoryService,
    tagService: TagService,
    selectedTags: List<Tag>,
    filter: (Tag) -> Boolean,
    onDismissRequest: () -> Unit,
    onValueChange: (Set<TagId>) -> Unit,
) {
    val selectedTagIds: Set<TagId> = remember(selectedTags) { selectedTags.map { it.id }.toSet() }
    val colorsByCategoryId: Map<TagCategoryId, Color> = remember {
        tagCategoryService.getAll().associateBy({ it.id }) { Color(it.color) }
    }
    val allTags: List<Tag> = remember { tagService.getAll() }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<Tag> = remember(selectedTags, searchText) {
        allTags.filter { !selectedTagIds.contains(it.id) && filter(it) && it.name.lowercase().contains(searchText) }
    }

    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest
    ) {
        PopupContent {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .width(600.dp)
                    .heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Selected ${selectedTags.size} tags:")
                ChipVerticalGrid {
                    selectedTags.forEach {
                        TagName(
                            tag = it,
                            color = colorsByCategoryId[it.categoryId],
                            onClick = { onValueChange(selectedTagIds - it.id) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.height(42.dp).padding(horizontal = 10.dp),
                ) {
                    SearchBar(
                        searchText = searchText,
                        onClearClick = { searchText = "" },
                        onSearchTextChanged = { searchText = it }
                    )
                }
                Text(text = "Found ${filteredTags.size} tags:")
                ChipVerticalGrid(modifier = Modifier.heightIn(max = 400.dp)) {
                    filteredTags.forEach {
                        TagName(
                            tag = it,
                            color = colorsByCategoryId[it.categoryId],
                            onClick = {
                                onValueChange(selectedTagIds + it.id)
                            }
                        )
                    }
                }
            }
        }
    }
}