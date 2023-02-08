package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagCategoryService
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText

@Composable
fun TagCategorySelector(
    tagCategoryService: TagCategoryService,
    categoryId: TagCategoryId? = null,
    filter: (TagCategory) -> Boolean = { true },
    onSelect: (TagCategoryId?) -> Unit
) {
    val category: TagCategory? by remember(categoryId) {
        mutableStateOf(categoryId?.let { tagCategoryService.getById(it) })
    }

    var showPopup: Boolean by remember { mutableStateOf(false) }

    DisableSelection {
        // Wrap in row and additional box to align top-end of button but outside
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.clickable { showPopup = true }) {
                if (category != null) {
                    TagCategoryName(category!!)
                } else {
                    FilledNameText("Not selected", MaterialTheme.colors.primary)
                }
            }
            if (showPopup) {
                Box {
                    SelectorPopup(tagCategoryService, filter, onDismissRequest = { showPopup = false }, onSelect)
                }
            }
        }
    }
}

@Composable
private fun SelectorPopup(
    tagCategoryService: TagCategoryService,
    filter: (TagCategory) -> Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (TagCategoryId) -> Unit
) {
    val allTagCategories = remember { tagCategoryService.getAll() }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTagCategories: List<TagCategory> = remember(searchText) {
        val loweredSearchText = searchText.lowercase()
        allTagCategories.filter { it.name.value.lowercase().contains(loweredSearchText) && filter(it) }
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
                    .padding(5.dp)
                    .width(600.dp)
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.height(42.dp),
                ) {
                    SearchBar(
                        searchText = searchText,
                        onClearClick = { searchText = "" },
                        onSearchTextChanged = { searchText = it }
                    )
                }
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