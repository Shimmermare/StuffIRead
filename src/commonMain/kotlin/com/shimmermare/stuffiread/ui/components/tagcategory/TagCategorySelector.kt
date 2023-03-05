package com.shimmermare.stuffiread.ui.components.tagcategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.shimmermare.stuffiread.tags.TagCategory
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagCategoryName
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageMode
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.TagCategoryForm
import com.shimmermare.stuffiread.ui.tagService

@Composable
fun TagCategorySelector(
    categoryId: TagCategoryId = TagCategoryId.None,
    filter: (TagCategory) -> Boolean = { true },
    onSelected: (TagCategoryId) -> Unit
) {
    var mode: Mode by remember { mutableStateOf(Mode.CLOSED) }

    SelectedCategory(categoryId, onShowSelectorRequest = { mode = Mode.SHOW_SELECTOR })

    when (mode) {
        Mode.SHOW_SELECTOR -> {
            Selector(
                initiallySelectedId = categoryId,
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
            QuickCreate(
                onShowSelectorRequest = { mode = Mode.SHOW_SELECTOR }
            )
        }

        else -> {}
    }
}

@Composable
private fun SelectedCategory(
    categoryId: TagCategoryId, onShowSelectorRequest: () -> Unit
) {
    val tagService = tagService

    val category: TagCategory? = remember(categoryId) {
        if (categoryId == TagCategoryId.None) null else tagService.getCategoryById(categoryId)
    }

    if (category != null) {
        TagCategoryName(category, onClick = onShowSelectorRequest)
    } else {
        FilledNameText(
            text = "Not selected",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.height(30.dp).clickable(onClick = onShowSelectorRequest)
        )
    }
}

@Composable
private fun Selector(
    initiallySelectedId: TagCategoryId,
    filter: (TagCategory) -> Boolean,
    onCloseRequest: () -> Unit,
    onSelected: (TagCategoryId) -> Unit,
    onShowQuickCreateRequest: () -> Unit,
) {
    val tagService = tagService

    val allCategories: List<TagCategory> = remember { tagService.getCategories() }

    var selected: TagCategory? by remember(initiallySelectedId) {
        mutableStateOf(if (initiallySelectedId != TagCategoryId.None) {
            allCategories.first { it.id == initiallySelectedId }
        } else {
            null
        })
    }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTagCategories: List<TagCategory> = remember(searchText) {
        val loweredSearchText = searchText.lowercase()
        allCategories
            .filter { it.id != selected?.id }
            .filter { it.name.value.lowercase().contains(loweredSearchText) && filter(it) }
    }

    FullscreenPopup {
        Column(
            modifier = Modifier.padding(10.dp).width(600.dp).heightIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Selected:")
            if (selected != null) {
                TagCategoryName(selected!!)
            } else {
                FilledNameText(
                    text = "Not selected",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.height(30.dp)
                )
            }

            SearchBar(searchText = searchText, onSearchTextChanged = { searchText = it })

            if (filteredTagCategories.isEmpty()) {
                Text("Nothing found")
            } else {
                Text("Found ${filteredTagCategories.size} category(s)")
            }

            ChipVerticalGrid(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                filteredTagCategories.forEach {
                    TagCategoryName(it, onClick = { selected = it })
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = onCloseRequest) {
                    Text("Cancel")
                }
                Button(enabled = initiallySelectedId != (selected?.id ?: TagCategoryId.None),
                    onClick = { onSelected(selected?.id ?: TagCategoryId.None) }) {
                    Text("Confirm")
                }
                Button(onClick = onShowQuickCreateRequest) {
                    Text("Quick create")
                }
            }
        }
    }
}

@Composable
private fun QuickCreate(onShowSelectorRequest: () -> Unit) {
    val tagService = tagService
    FullscreenPopup {
        TagCategoryForm(
            mode = EditTagCategoryPageMode.CREATE,
            category = TagCategory(name = TagCategoryName("New category")),
            onBack = onShowSelectorRequest,
            onSubmit = {
                tagService.createCategory(it)
                onShowSelectorRequest()
            }
        )
    }
}

private enum class Mode {
    CLOSED, SHOW_SELECTOR, SHOW_QUICK_CREATE
}