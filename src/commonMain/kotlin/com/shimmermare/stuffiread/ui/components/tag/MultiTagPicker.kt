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
import androidx.compose.material.MaterialTheme
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
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.layout.PointerInsideTrackerBox
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar

/**
 * Pick multiple tags.
 */
@Composable
fun MultiTagPicker(
    title: String,
    pickedTagIds: Set<TagId>,
    filter: (Tag) -> Boolean = { true },
    defaultOpenPopup: Boolean = false,
    onPick: (Set<TagId>) -> Unit,
) {
    var openPopup: Boolean by remember { mutableStateOf(defaultOpenPopup) }

    PickedTagsField(
        pickedTagIds = pickedTagIds,
        onUnpickRequest = { onPick(pickedTagIds - it) },
        onOpenPopupRequest = { openPopup = true }
    )

    if (openPopup) {
        MultiPickerPopup(
            title = title,
            currentlyPickedTagIds = pickedTagIds,
            filter = filter,
            onCloseRequest = { openPopup = false },
            onPicked = {
                openPopup = false
                onPick(it)
            }
        )
    }
}

@Composable
private fun PickedTagsField(
    pickedTagIds: Set<TagId>,
    onUnpickRequest: (TagId) -> Unit,
    onOpenPopupRequest: () -> Unit
) {
    val pickedTags: List<TagWithCategory> = remember(pickedTagIds) {
        StoryArchiveHolder.tagService.getTagsWithCategoryByIds(pickedTagIds).sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    ChipVerticalGrid {
        pickedTags.forEach { tag ->
            PickedTag(tag) { onUnpickRequest(tag.tag.id) }
        }
        Box(modifier = Modifier.clickable(onClick = onOpenPopupRequest)) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun PickedTag(
    tag: TagWithCategory,
    onUnselect: () -> Unit
) {
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
private fun MultiPickerPopup(
    title: String,
    currentlyPickedTagIds: Set<TagId>,
    filter: (Tag) -> Boolean,
    onCloseRequest: () -> Unit,
    onPicked: (Set<TagId>) -> Unit,
) {
    // Reload all tags after quick create
    var allTagsDirtyCounter: Int by remember { mutableStateOf(0) }
    val allTags = remember(allTagsDirtyCounter) {
        StoryArchiveHolder.tagService.getTagsWithCategory().sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    var showQuickCreate: Boolean by remember { mutableStateOf(false) }

    var pickedTagIds: Set<TagId> by remember(currentlyPickedTagIds) {
        mutableStateOf(currentlyPickedTagIds)
    }
    val pickedTags: List<TagWithCategory> = remember(allTagsDirtyCounter, pickedTagIds) {
        allTags.filter { pickedTagIds.contains(it.tag.id) }
    }

    var searchText: String by remember { mutableStateOf("") }
    val availableToPickTags: List<TagWithCategory> = remember(allTagsDirtyCounter, pickedTagIds, searchText) {
        val searchTextLowered = searchText.trim().lowercase()
        allTags.filter {
            val tag = it.tag
            !pickedTagIds.contains(tag.id) && filter(tag) && tag.name.value.lowercase().contains(searchTextLowered)
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
                Text(title, style = MaterialTheme.typography.h6)
                Text(text = "Picked ${pickedTags.size} tags:")
                ChipVerticalGrid {
                    pickedTags.forEach { tag ->
                        TagName(
                            tag = tag,
                            onClick = { pickedTagIds = pickedTagIds - tag.tag.id }
                        )
                    }
                }
                SearchBar(
                    searchText = searchText,
                    onSearchTextChanged = { searchText = it }
                )
                Text(text = "Found ${availableToPickTags.size} tags:")
                ChipVerticalGrid(modifier = Modifier.heightIn(max = 400.dp)) {
                    availableToPickTags.forEach {
                        TagName(
                            tag = it,
                            onClick = { pickedTagIds = pickedTagIds + it.tag.id }
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
                        enabled = currentlyPickedTagIds != pickedTagIds,
                        onClick = { onPicked(pickedTagIds) }
                    ) {
                        Text("Confirm")
                    }
                    Button(onClick = { showQuickCreate = true }) {
                        Text("Quick create")
                    }
                }
            }
        }
    }

    if (showQuickCreate) {
        QuickCreateTag(
            onCloseRequest = { showQuickCreate = false },
            onCreate = {
                allTagsDirtyCounter++
                showQuickCreate = false
            }
        )
    }
}