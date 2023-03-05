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
import androidx.compose.foundation.text.selection.DisableSelection
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
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.layout.PointerInsideTrackerBox
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.tagService

/**
 * Input to select multiple unique tags from list of all tags.
 *
 *
 * @param filter additional filter for tags available for selection.
 * @param onSelect will be called when popup is dismissed.
 */
@Composable
fun MultiTagSelector(
    selectedIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    onSelect: (Set<TagId>) -> Unit
) {
    val tagService = tagService

    var showPopup: Boolean by remember { mutableStateOf(false) }

    LoadingContainer(
        key = selectedIds,
        loader = { ids -> tagService.getTagsWithCategoryByIds(ids).associateBy { it.tag.id } }
    ) { selectedTags ->
        DisableSelection {
            if (showPopup) {
                Box {
                    LoadingContainer(
                        key = selectedTags,
                        loader = { tagService.getTagsWithCategory() }
                    ) { allTags ->
                        SelectorPopup(
                            selectedTags,
                            allTags,
                            filter = { filter(it.tag) },
                            onSelected = {
                                if (it != selectedTags.keys) {
                                    onSelect(selectedTags.keys)
                                }
                                showPopup = false
                            }
                        )
                    }
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
}

@Composable
private fun SelectedTagName(tag: TagWithCategory, onUnselect: () -> Unit) {
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
private fun SelectorPopup(
    initiallySelectedTags: Map<TagId, TagWithCategory>,
    allTags: List<TagWithCategory>,
    filter: (TagWithCategory) -> Boolean,
    onSelected: (Set<TagId>) -> Unit,
) {
    var selectedTags: Map<TagId, TagWithCategory> by remember(initiallySelectedTags.keys) {
        mutableStateOf(initiallySelectedTags)
    }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<TagWithCategory> = remember(selectedTags, searchText) {
        allTags.filter {
            !selectedTags.containsKey(it.tag.id) && filter(it)
                    && it.tag.name.value.lowercase().contains(searchText)
        }
    }

    Popup(
        focusable = true,
        onDismissRequest = { onSelected(selectedTags.keys) }
    ) {
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
            }
        }
    }
}