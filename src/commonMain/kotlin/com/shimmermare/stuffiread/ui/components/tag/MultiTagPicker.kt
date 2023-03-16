package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.input.SizedIconButton
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.layout.PickerWithSearchLayout
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
    onPick: (Set<TagId>) -> Unit,
) {
    var openPopup: Boolean by remember { mutableStateOf(false) }

    PickedTagsField(
        pickedTagIds = pickedTagIds,
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
    onOpenPopupRequest: () -> Unit
) {
    val pickedTags: List<TagWithCategory> = remember(pickedTagIds) {
        StoryArchiveHolder.tagService.getTagsWithCategoryByIds(pickedTagIds).sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    if (pickedTags.isEmpty()) {
        IconButton(onClick = onOpenPopupRequest) {
            Icon(Icons.Filled.Edit, null)
        }
    } else {
        ChipVerticalGrid {
            pickedTags.forEach { tag ->
                TagName(tag)
            }
            SizedIconButton(onClick = onOpenPopupRequest, size = 30.dp) {
                Icon(Icons.Filled.Edit, null)
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
            PickerWithSearchLayout(
                title = title,
                pickedItems = {
                    if (pickedTags.isNotEmpty()) {
                        Text(text = "Picked ${pickedTags.size} tag(s):")
                        ChipVerticalGrid {
                            pickedTags.forEach { tag ->
                                TagName(
                                    tag = tag,
                                    onClick = { pickedTagIds = pickedTagIds - tag.tag.id }
                                )
                            }
                        }
                    } else {
                        Text("No tag(s) picked.")
                    }
                },
                searchBar = {
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChanged = { searchText = it }
                    )
                },
                availableToPickItems = {
                    if (availableToPickTags.isNotEmpty()) {
                        Text(text = "Found ${availableToPickTags.size} tag(s):")
                        ChipVerticalGrid {
                            availableToPickTags.forEach {
                                TagName(
                                    tag = it,
                                    onClick = { pickedTagIds = pickedTagIds + it.tag.id }
                                )
                            }
                        }
                    } else if (allTags.isEmpty()) {
                        Text("No tag(s) exist to pick.")
                    } else {
                        Text("No tag(s) found.")
                    }
                },
                actionButtons = {
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
            )
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