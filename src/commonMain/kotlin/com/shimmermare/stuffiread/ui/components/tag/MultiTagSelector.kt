package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.layout.PointerInsideTrackerBox
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.tagService

/**
 * Input to select multiple unique tags from list of all tags.
 *
 *
 * @param filter additional filter for tags available for selection.
 */
@Composable
fun MultiTagSelector(
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onSelected: (Set<TagId>) -> Unit
) {
    var mode: Mode by remember { mutableStateOf(Mode.CLOSED) }

    SelectedTags(
        selectedIds = selectedIds,
        onUnselectRequest = { onSelected(selectedIds - it) },
        onShowSelectorRequest = { mode = Mode.SHOW_SELECTOR }
    )

    when (mode) {
        Mode.SHOW_SELECTOR -> {
            Selector(
                selectedIds = selectedIds,
                filter = filter,
                onCloseRequest = { mode = Mode.CLOSED },
                onSelected = {
                    onSelected(it)
                    mode = Mode.CLOSED
                },
                onShowQuickCreateRequest = { mode = Mode.SHOW_QUICK_CREATE }
            )
        }

        Mode.SHOW_QUICK_CREATE -> {
            QuickCreateTag(
                onShowSelectorRequest = { mode = Mode.SHOW_SELECTOR }
            )
        }

        else -> {}
    }
}

@Composable
private fun SelectedTags(
    selectedIds: Set<TagId>,
    onUnselectRequest: (TagId) -> Unit,
    onShowSelectorRequest: () -> Unit
) {
    val tagService = tagService

    val selectedTags: List<TagWithCategory> = remember(selectedIds) {
        tagService.getTagsWithCategoryByIds(selectedIds).sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    ChipVerticalGrid {
        selectedTags.forEach { tag ->
            SelectedTag(tag) { onUnselectRequest(tag.tag.id) }
        }

        Box(modifier = Modifier.clickable(onClick = onShowSelectorRequest)) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun SelectedTag(tag: TagWithCategory, onUnselect: () -> Unit) {
    PointerInsideTrackerBox { pointerInside ->
        Row(
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
}

@Composable
private fun Selector(
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onCloseRequest: () -> Unit,
    onSelected: (Set<TagId>) -> Unit,
    onShowQuickCreateRequest: () -> Unit,
) {
    val tagService = tagService
    val allTags = remember { tagService.getTagsWithCategory() }
    SelectorPopup(
        selectedIds,
        allTags,
        filter = { filter(it.tag) },
        onCloseRequest = onCloseRequest,
        onSelected = onSelected,
        onShowQuickCreateRequest = onShowQuickCreateRequest
    )
}

@Composable
private fun SelectorPopup(
    initiallySelectedIds: Set<TagId>,
    allTags: List<TagWithCategory>,
    filter: (TagWithCategory) -> Boolean,
    onCloseRequest: () -> Unit,
    onSelected: (Set<TagId>) -> Unit,
    onShowQuickCreateRequest: () -> Unit,
) {
    var selectedTags: Map<TagId, TagWithCategory> by remember(initiallySelectedIds) {
        mutableStateOf(allTags.filter { initiallySelectedIds.contains(it.tag.id) }.associateBy { it.tag.id })
    }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<TagWithCategory> = remember(selectedTags, searchText) {
        allTags.filter {
            !selectedTags.containsKey(it.tag.id) && filter(it)
                    && it.tag.name.value.lowercase().contains(searchText)
        }
    }

    FullscreenPopup {
        PopupContent {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .width(600.dp)
                    .heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Selected ${selectedTags.size} tags:")
                ChipVerticalGrid {
                    selectedTags.forEach { (id, tag) ->
                        TagName(
                            tag = tag,
                            onClick = { selectedTags = selectedTags - id }
                        )
                    }
                }
                SearchBar(
                    searchText = searchText,
                    onSearchTextChanged = { searchText = it }
                )
                Text(text = "Found ${filteredTags.size} tags:")
                ChipVerticalGrid(modifier = Modifier.heightIn(max = 400.dp)) {
                    filteredTags.forEach {
                        TagName(
                            tag = it,
                            onClick = { selectedTags = selectedTags + (it.tag.id to it) }
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = onCloseRequest) {
                        Text("Cancel")
                    }
                    Button(
                        enabled = initiallySelectedIds != selectedTags.keys,
                        onClick = { onSelected(selectedTags.keys) }
                    ) {
                        Text("Confirm")
                    }
                    Button(onClick = onShowQuickCreateRequest) {
                        Text("Quick create")
                    }
                }
            }
        }
    }
}

private enum class Mode {
    CLOSED,
    SHOW_SELECTOR,
    SHOW_QUICK_CREATE
}