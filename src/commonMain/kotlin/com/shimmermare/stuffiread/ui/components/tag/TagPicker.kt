package com.shimmermare.stuffiread.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.layout.PickerWithSearchLayout
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString

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
            Strings.components_tagPicker_pickerButton.remember(),
            MaterialTheme.colors.primary,
            modifier = Modifier.height(DefaultTagNameHeight).clickable(onClick = onOpenPopupRequest)
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
            PickerWithSearchLayout(
                title = title,
                pickedItems = {
                    if (pickedTag != null) {
                        Text(Strings.components_tagPicker_pickedTag.remember())
                        TagName(pickedTag, onClick = { pickedTagId = TagId.None })
                    } else {
                        Text(Strings.components_tagPicker_notPicked.remember())
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
                        Text(components_tagPicker_search_found.remember(availableToPickTags.size))
                        ChipVerticalGrid {
                            availableToPickTags.forEach {
                                TagName(
                                    tag = it,
                                    onClick = { pickedTagId = it.tag.id }
                                )
                            }
                        }
                    } else if (allTags.isEmpty()) {
                        Text(Strings.components_tagPicker_search_noExisting.remember())
                    } else {
                        Text(Strings.components_tagPicker_search_notFound.remember())
                    }
                },
                actionButtons = {
                    Button(onClick = onCloseRequest) {
                        Text(Strings.components_tagPicker_cancelButton.remember())
                    }
                    Button(
                        enabled = currentlyPickedTagId != pickedTagId,
                        onClick = { onPicked(pickedTagId) }
                    ) {
                        Text(Strings.components_tagPicker_confirmButton.remember())
                    }
                    Button(onClick = { showQuickCreate = true }) {
                        Text(Strings.components_tagPicker_quickCreateButton.remember())
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

val components_tagPicker_search_found = PluralLocalizedString(
    Strings.components_tagPicker_search_found_other,
    Strings.components_tagPicker_search_found_one,
    Strings.components_tagPicker_search_found_two,
    Strings.components_tagPicker_search_found_few,
    Strings.components_tagPicker_search_found_many,
    Strings.components_tagPicker_search_found_other,
)