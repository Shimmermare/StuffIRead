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
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.util.LoadingContainer

/**
 * Input to select multiple unique tags from list of all tags.
 *
 *
 * @param filter additional filter for tags available for selection.
 * @param onSelect will be called when popup is dismissed.
 */
@Composable
fun MultiTagSelector(
    tagService: TagService,
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onSelect: (Set<TagId>) -> Unit
) {
    LoadingContainer(
        key = selectedIds,
        loader = { ids -> tagService.getTagsWithCategoryByIds(ids).associateBy { it.tag.id } }
    ) { selectedTags ->
        SelectorContent(
            tagService,
            selectedTags,
            filter,
            onSelect
        )
    }
}

@Composable
private fun SelectorContent(
    tagService: TagService,
    initiallySelectedTags: Map<TagId, TagWithCategory>,
    filter: (Tag) -> Boolean,
    onSelect: (Set<TagId>) -> Unit
) {
    var selectedTags: Map<TagId, TagWithCategory> by remember { mutableStateOf(initiallySelectedTags) }
    var showPopup: Boolean by remember(initiallySelectedTags) { mutableStateOf(false) }

    DisableSelection {
        if (showPopup) {
            Box {
                SelectorPopupContainer(
                    tagService,
                    selectedTags,
                    filter = { filter(it.tag) },
                    onDismissRequest = {
                        if (initiallySelectedTags != selectedTags) {
                            onSelect(selectedTags.keys)
                        }
                        showPopup = false
                    },
                    onValueChange = {
                        selectedTags = it
                    }
                )
            }
        }
        ChipVerticalGrid {
            selectedTags.forEach { (id, tag) ->
                SelectedTagName(tag) { onSelect(selectedTags.keys - id) }
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
private fun SelectorPopupContainer(
    tagService: TagService,
    selectedTags: Map<TagId, TagWithCategory>,
    filter: (TagWithCategory) -> Boolean,
    onDismissRequest: () -> Unit,
    onValueChange: (Map<TagId, TagWithCategory>) -> Unit,
) {
    LoadingContainer(
        key = selectedTags,
        loader = { tagService.getTagsWithCategory() }
    ) { allTags ->
        SelectorPopup(
            selectedTags,
            allTags,
            filter,
            onDismissRequest,
            onValueChange
        )
    }
}

@Composable
private fun SelectorPopup(
    selectedTags: Map<TagId, TagWithCategory>,
    allTags: List<TagWithCategory>,
    filter: (TagWithCategory) -> Boolean,
    onDismissRequest: () -> Unit,
    onValueChange: (Map<TagId, TagWithCategory>) -> Unit,
) {
    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<TagWithCategory> = remember(selectedTags, searchText) {
        allTags.filter {
            !selectedTags.containsKey(it.tag.id) && filter(it)
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
                    selectedTags.forEach { (id, tag) ->
                        TagName(
                            tag = tag,
                            onClick = { onValueChange(selectedTags - id) }
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
                            onClick = { onValueChange(selectedTags + (it.tag.id to it)) }
                        )
                    }
                }
            }
        }
    }
}