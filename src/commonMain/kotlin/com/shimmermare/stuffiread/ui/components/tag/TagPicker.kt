package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText

@Composable
fun TagPicker(
    title: String,
    pickedTagId: TagId,
    filter: (Tag) -> Boolean = { true },
    onPick: (TagId) -> Unit,
) {
    var openPopup: Boolean by remember { mutableStateOf(false) }

    PickedTag(pickedTagId, onOpenPopupRequest = { openPopup = true })

    if (openPopup) {
        PickerPopup(
            title = title,
            currentlyPickedTagId = pickedTagId,
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
private fun PickedTag(pickedTagId: TagId, onOpenPopupRequest: () -> Unit) {
    val pickedTag = remember(pickedTagId) {
        if (pickedTagId == TagId.None) null else tagService.getTagWithCategoryById(pickedTagId)
    }
    if (pickedTag != null) {
        TagName(pickedTag, onClick = onOpenPopupRequest)
    } else {
        FilledNameText(
            "Click to pick",
            MaterialTheme.colors.primary,
            modifier = Modifier.height(30.dp).clickable(onClick = onOpenPopupRequest)
        )
    }
}

@Composable
private fun PickerPopup(
    title: String,
    currentlyPickedTagId: TagId,
    filter: (Tag) -> Boolean,
    onCloseRequest: () -> Unit,
    onPicked: (TagId) -> Unit,
) {
    // Reload all tags after quick create
    var allTagsDirtyCounter: Int by remember { mutableStateOf(0) }
    val allTags = remember(allTagsDirtyCounter) {
        tagService.getTagsWithCategory().sortedWith(TagWithCategory.DEFAULT_ORDER)
    }

    var showQuickCreate: Boolean by remember { mutableStateOf(false) }

    var pickedTagId: TagId by remember(currentlyPickedTagId) {
        mutableStateOf(currentlyPickedTagId)
    }
    val pickedTag: TagWithCategory? = remember(allTagsDirtyCounter, pickedTagId) {
        tagService.getTagWithCategoryById(pickedTagId)
    }

    var searchText: String by remember { mutableStateOf("") }
    val availableToPickTags: List<TagWithCategory> = remember(allTagsDirtyCounter, pickedTagId, searchText) {
        val searchTextLowered = searchText.trim().lowercase()
        allTags.filter {
            pickedTagId != it.tag.id && filter(it.tag) && it.tag.name.value.lowercase().contains(searchTextLowered)
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
                if (pickedTag != null) {
                    Text(text = "Picked tag:")
                    TagName(pickedTag, onClick = { pickedTagId = TagId.None })
                } else {
                    Text(text = "Tag not picked")
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
                            onClick = { pickedTagId = it.tag.id }
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
                        enabled = currentlyPickedTagId != pickedTagId,
                        onClick = { onPicked(pickedTagId) }
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