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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.domain.tags.*
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar

/**
 * Input to select multiple unique tags from list of all tags.
 *
 * @param filter additional filter for tags available for selection.
 */
@Composable
fun MultiTagSelector(
    tagService: TagService,
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onValueChange: (Set<TagId>) -> Unit
) {
    val selectedTags: List<TagWithCategory> = remember(selectedIds) { tagService.getByIdsWithCategory(selectedIds) }

    var showPopup: Boolean by remember { mutableStateOf(false) }

    DisableSelection {
        if (showPopup) {
            Box {
                SelectorPopup(
                    tagService,
                    selectedTags,
                    filter = { filter(it.tag) },
                    onDismissRequest = { showPopup = false },
                    onValueChange
                )
            }
        }
        ChipVerticalGrid {
            selectedTags.forEach {
                SelectedTagName(it) { onValueChange(selectedIds - it.tag.id) }
            }

            Box(modifier = Modifier.clickable { showPopup = true }) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SelectedTagName(tag: TagWithCategory, onUnselect: () -> Unit) {
    var pointerInside by remember(tag.tag.id) { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) { pointerInside = true }
            .onPointerEvent(PointerEventType.Exit) { pointerInside = false },
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TagName(tag)
        if (pointerInside) {
            Box(modifier = Modifier.clickable(onClick = onUnselect)) {
                Icon(Icons.Filled.Clear, null, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
private fun SelectorPopup(
    tagService: TagService,
    selectedTags: List<TagWithCategory>,
    filter: (TagWithCategory) -> Boolean,
    onDismissRequest: () -> Unit,
    onValueChange: (Set<TagId>) -> Unit,
) {
    val selectedTagIds: Set<TagId> = remember(selectedTags) { selectedTags.map { it.tag.id }.toSet() }
    val allTags: List<TagWithCategory> = remember { tagService.getAllWithCategories() }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<TagWithCategory> = remember(selectedTags, searchText) {
        allTags.filter {
            !selectedTagIds.contains(it.tag.id) && filter(it)
                    && it.tag.name.value.lowercase().contains(searchText)
        }
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
                            onClick = { onValueChange(selectedTagIds - it.tag.categoryId) }
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
                            onClick = {
                                onValueChange(selectedTagIds + it.tag.id)
                            }
                        )
                    }
                }
            }
        }
    }
}