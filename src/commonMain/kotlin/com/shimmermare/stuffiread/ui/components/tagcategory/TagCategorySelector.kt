package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText

@Composable
fun TagCategorySelector(
    tagService: TagService,
    categoryId: TagCategoryId? = null,
    filter: (TagCategory) -> Boolean = { true },
    onSelect: (TagCategoryId?) -> Unit
) {
    val category: TagCategory? = remember(categoryId) { categoryId?.let { tagService.getCategoryById(it) } }

    var showPopup: Boolean by remember { mutableStateOf(false) }

    DisableSelection {
        // Wrap in row and additional box to align top-end of button but outside
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.clickable { showPopup = true }) {
                if (category != null) {
                    TagCategoryName(category)
                } else {
                    FilledNameText("Not selected", MaterialTheme.colors.primary)
                }
            }
            if (showPopup) {
                Box {
                    SelectorPopup(tagService, filter, onDismissRequest = { showPopup = false }, onSelect)
                }
            }
        }
    }
}

@Composable
private fun SelectorPopup(
    tagService: TagService,
    filter: (TagCategory) -> Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (TagCategoryId) -> Unit,
) {
    val allCategories = remember { tagService.getCategories() }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTagCategories: List<TagCategory> = remember(searchText) {
        val loweredSearchText = searchText.lowercase()
        allCategories.filter { it.name.value.lowercase().contains(loweredSearchText) && filter(it) }
    }

    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(1))
                .background(color = MaterialTheme.colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .width(600.dp)
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SearchBar(
                    searchText = searchText,
                    onClearClick = { searchText = "" },
                    onSearchTextChanged = { searchText = it }
                )
                ChipVerticalGrid(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    filteredTagCategories.forEach {
                        TagCategoryName(it, onClick = {
                            onDismissRequest()
                            onSelect(it.id)
                        })
                    }
                }
            }
        }
    }
}