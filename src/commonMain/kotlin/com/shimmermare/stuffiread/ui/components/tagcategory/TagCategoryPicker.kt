package com.shimmermare.stuffiread.ui.components.tagcategory

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
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
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
fun TagCategoryPicker(
    title: String,
    pickedCategoryId: TagCategoryId,
    filter: (TagCategory) -> Boolean = { true },
    onPick: (TagCategoryId) -> Unit,
) {
    var openPopup: Boolean by remember(pickedCategoryId) { mutableStateOf(false) }

    PickedTagCategory(pickedCategoryId, onOpenPopupRequest = { openPopup = true })

    if (openPopup) {
        PickerPopup(
            title = title,
            currentlyPickedCategoryId = pickedCategoryId,
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
private fun PickedTagCategory(pickedCategoryId: TagCategoryId, onOpenPopupRequest: () -> Unit) {
    val pickedCategory = remember(pickedCategoryId) {
        if (pickedCategoryId == TagCategoryId.None) null else tagService.getCategoryById(pickedCategoryId)
    }
    if (pickedCategory != null) {
        TagCategoryName(pickedCategory, onClick = onOpenPopupRequest)
    } else {
        FilledNameText(
            Strings.components_tagCategoryPicker_pickerButton.remember(),
            MaterialTheme.colors.primary,
            modifier = Modifier.height(DefaultCategoryNameHeight).clickable(onClick = onOpenPopupRequest),
        )
    }
}

@Composable
private fun PickerPopup(
    title: String,
    currentlyPickedCategoryId: TagCategoryId,
    filter: (TagCategory) -> Boolean,
    onCloseRequest: () -> Unit,
    onPicked: (TagCategoryId) -> Unit,
) {
    // Reload all categories after quick create
    var allCategoriesDirtyCounter: Int by remember { mutableStateOf(0) }
    val allCategories = remember(allCategoriesDirtyCounter) {
        tagService.getCategories().sortedWith(TagCategory.DEFAULT_ORDER)
    }

    var showQuickCreate: Boolean by remember { mutableStateOf(false) }

    var pickedCategoryId: TagCategoryId by remember(currentlyPickedCategoryId) {
        mutableStateOf(currentlyPickedCategoryId)
    }
    val pickedCategory: TagCategory? = remember(allCategoriesDirtyCounter, pickedCategoryId) {
        tagService.getCategoryById(pickedCategoryId)
    }

    var searchText: String by remember { mutableStateOf("") }
    val availableToPickCategories = remember(allCategoriesDirtyCounter, pickedCategoryId, searchText) {
        val searchTextLowered = searchText.trim().lowercase()
        allCategories.filter {
            pickedCategoryId != it.id && filter(it) && it.name.value.lowercase().contains(searchTextLowered)
        }
    }

    FullscreenPopup {
        PopupContent {
            PickerWithSearchLayout(
                title = title,
                pickedItems = {
                    if (pickedCategory != null) {
                        Text(Strings.components_tagCategoryPicker_pickedTag.remember())
                        TagCategoryName(pickedCategory, onClick = { pickedCategoryId = TagCategoryId.None })
                    } else {
                        Text(Strings.components_tagCategoryPicker_notPicked.remember())
                    }
                },
                searchBar = {
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChanged = { searchText = it }
                    )
                },
                availableToPickItems = {
                    if (availableToPickCategories.isNotEmpty()) {
                        Text(components_tagCategoryPicker_search_found.remember(availableToPickCategories.size))
                        ChipVerticalGrid {
                            availableToPickCategories.forEach {
                                TagCategoryName(it, onClick = { pickedCategoryId = it.id })
                            }
                        }
                    } else if (allCategories.isEmpty()) {
                        Text(Strings.components_tagCategoryPicker_search_noExisting.remember())
                    } else {
                        Text(Strings.components_tagCategoryPicker_search_notFound.remember())
                    }
                },
                actionButtons = {
                    Button(onClick = onCloseRequest) {
                        Text(Strings.components_tagCategoryPicker_cancelButton.remember())
                    }
                    Button(
                        enabled = currentlyPickedCategoryId != pickedCategoryId,
                        onClick = { onPicked(pickedCategoryId) }
                    ) {
                        Text(Strings.components_tagCategoryPicker_confirmButton.remember())
                    }
                    Button(onClick = { showQuickCreate = true }) {
                        Text(Strings.components_tagCategoryPicker_quickCreateButton.remember())
                    }
                }
            )
        }
    }

    if (showQuickCreate) {
        QuickCreateTagCategory(
            onCloseRequest = { showQuickCreate = false },
            onCreate = {
                allCategoriesDirtyCounter++
                showQuickCreate = false
            }
        )
    }
}

private val components_tagCategoryPicker_search_found = PluralLocalizedString(
    Strings.components_tagCategoryPicker_search_found_other,
    Strings.components_tagCategoryPicker_search_found_one,
    Strings.components_tagCategoryPicker_search_found_two,
    Strings.components_tagCategoryPicker_search_found_few,
    Strings.components_tagCategoryPicker_search_found_many,
    Strings.components_tagCategoryPicker_search_found_other,
)