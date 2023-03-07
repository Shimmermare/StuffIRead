package com.shimmermare.stuffiread.ui.components.tag

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
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagWithCategory
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.layout.ChipVerticalGrid
import com.shimmermare.stuffiread.ui.components.layout.FullscreenPopup
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.text.FilledNameText
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode
import com.shimmermare.stuffiread.ui.pages.tag.edit.TagForm

@Composable
fun TagSelector(
    tagId: TagId = TagId.None,
    filter: (Tag) -> Boolean = { true },
    onSelected: (TagId) -> Unit
) {
    var mode: TagSelectorMode by remember { mutableStateOf(TagSelectorMode.CLOSED) }

    SelectedTag(tagId, onShowSelectorRequest = { mode = TagSelectorMode.SHOW_SELECTOR })

    when (mode) {
        TagSelectorMode.SHOW_SELECTOR -> {
            Selector(
                initiallySelectedId = tagId,
                filter = filter,
                onCloseRequest = { mode = TagSelectorMode.CLOSED },
                onSelected = {
                    onSelected(it)
                    mode = TagSelectorMode.CLOSED
                },
                onShowQuickCreateRequest = { mode = TagSelectorMode.SHOW_QUICK_CREATE }
            )
        }

        TagSelectorMode.SHOW_QUICK_CREATE -> {
            QuickCreateTag(
                onShowSelectorRequest = { mode = TagSelectorMode.SHOW_SELECTOR }
            )
        }

        else -> {}
    }
}

@Composable
private fun SelectedTag(
    tagId: TagId, onShowSelectorRequest: () -> Unit
) {

    val tag: TagWithCategory? = remember(tagId) {
        if (tagId == TagId.None) null else tagService.getTagWithCategoryById(tagId)
    }

    if (tag != null) {
        TagName(tag, onClick = onShowSelectorRequest)
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
    initiallySelectedId: TagId,
    filter: (Tag) -> Boolean,
    onCloseRequest: () -> Unit,
    onSelected: (TagId) -> Unit,
    onShowQuickCreateRequest: () -> Unit,
) {

    val allTags: List<TagWithCategory> = remember { tagService.getTagsWithCategory() }

    var selected: TagWithCategory? by remember(initiallySelectedId) {
        mutableStateOf(if (initiallySelectedId != TagId.None) {
            allTags.first { it.tag.id == initiallySelectedId }
        } else {
            null
        })
    }

    var searchText: String by remember { mutableStateOf("") }
    val filteredTags: List<TagWithCategory> = remember(searchText) {
        val loweredSearchText = searchText.lowercase()
        allTags
            .filter { it.tag.id != selected?.tag?.id }
            .filter { it.tag.name.value.lowercase().contains(loweredSearchText) && filter(it.tag) }
    }

    FullscreenPopup {
        Column(
            modifier = Modifier.padding(10.dp).width(600.dp).heightIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Selected:")
            if (selected != null) {
                TagName(selected!!)
            } else {
                FilledNameText(
                    text = "Not selected",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.height(30.dp)
                )
            }

            SearchBar(searchText = searchText, onSearchTextChanged = { searchText = it })

            if (filteredTags.isEmpty()) {
                Text("Nothing found")
            } else {
                Text("Found ${filteredTags.size} category(s)")
            }

            ChipVerticalGrid(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                filteredTags.forEach {
                    TagName(it, onClick = { selected = it })
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = onCloseRequest) {
                    Text("Cancel")
                }
                Button(enabled = initiallySelectedId != (selected?.tag?.id ?: TagId.None),
                    onClick = { onSelected(selected?.tag?.id ?: TagId.None) }) {
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
fun QuickCreateTag(onShowSelectorRequest: () -> Unit) {
    FullscreenPopup {
        TagForm(
            mode = EditTagPageMode.CREATE,
            tag = Tag(
                name = com.shimmermare.stuffiread.tags.TagName("New tag"),
                categoryId = TagCategoryId.None,
            ),
            modifier = Modifier.padding(20.dp).width(800.dp),
            onBack = onShowSelectorRequest,
            onSubmit = {
                tagService.createTag(it)
                onShowSelectorRequest()
            }
        )
    }
}

private enum class TagSelectorMode {
    CLOSED, SHOW_SELECTOR, SHOW_QUICK_CREATE
}