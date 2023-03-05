package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.components.layout.LoadingContainer
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.storySearchService
import com.shimmermare.stuffiread.ui.storyService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * Input to select multiple stories with search by name.
 *
 * @param onSelect will be called when popup is dismissed.
 */
@Composable
fun MultiStorySelector(
    selectedIds: Set<StoryId>,
    filter: (Story) -> Boolean = { true },
    onSelect: (Set<StoryId>) -> Unit
) {
    val storyService = storyService

    var showPopup: Boolean by remember { mutableStateOf(false) }

    LoadingContainer(
        key = selectedIds,
        loader = { ids -> storyService.getStoriesByIds(ids).toList().sortedBy { it.name } }
    ) { stories ->
        DisableSelection {
            if (showPopup) {
                Box {
                    SelectorPopup(
                        stories,
                        filter,
                        onSelected = {
                            showPopup = false
                            if (selectedIds != it) onSelect(it)
                        }
                    )
                }
            }
            Column(
                modifier = Modifier.width(600.dp)
            ) {
                stories.forEach { story ->
                    SelectedStoryName(story) { onSelect(selectedIds - story.id) }
                }
                IconButton(onClick = { showPopup = true }) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        }
    }
}

@Composable
private fun SelectedStoryName(story: Story, onUnselect: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1F)) {
            SmallStoryCardWithPreview(story)
        }
        IconButton(onClick = onUnselect) {
            Icon(Icons.Filled.Clear, null)
        }
    }
}

@Composable
private fun SelectorPopup(
    initiallySelectedStories: List<Story>,
    filter: (Story) -> Boolean,
    onSelected: (Set<StoryId>) -> Unit,
) {
    val storySearchService = storySearchService
    val coroutineScope = rememberCoroutineScope()

    var selectedStories: Map<StoryId, Story> by remember(initiallySelectedStories) {
        mutableStateOf(initiallySelectedStories.associateBy { it.id })
    }
    val selectedStoriesSorted: List<Story> = remember(selectedStories.keys) {
        selectedStories.values.sortedBy { it.name }
    }

    var searchText: String by remember { mutableStateOf("") }
    var searchTextUsed: String by remember { mutableStateOf("") }
    val foundStories: MutableList<Story> = remember { mutableStateListOf() }

    Popup(
        focusable = true,
        onDismissRequest = { onSelected(selectedStories.keys) }
    ) {
        PopupContent {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .width(800.dp)
                    .heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("${selectedStoriesSorted.size} story(s) selected", fontWeight = FontWeight.Bold)
                selectedStoriesSorted.forEach {
                    SmallStoryCard(it, onClick = { selectedStories = selectedStories - it.id })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1F)
                    ) {
                        SearchBar(
                            searchText = searchText,
                            placeholderText = "Search by name",
                            onSearchTextChanged = { searchText = it.ifBlank { "" } }
                        )
                    }
                    Button(
                        enabled = searchText.isNotBlank() && searchText != searchTextUsed,
                        onClick = {
                            coroutineScope.launch {
                                searchTextUsed = searchText
                                foundStories.clear()
                                val searchFilter = StoryFilter(nameContains = searchTextUsed)
                                storySearchService.getStoriesByFilter(searchFilter)
                                    .filter(filter)
                                    .onEach { story ->
                                        foundStories.add(story)
                                        foundStories.sortBy { it.name }
                                    }.collect()
                            }
                        }
                    ) {
                        Text("Search")
                    }
                }
                if (searchTextUsed.isBlank()) {
                    Text("Click Search to find stories")
                } else {
                    val foundToShow by derivedStateOf { foundStories.filter { !selectedStories.containsKey(it.id) } }
                    Text("${foundToShow.size} story(s) found by '$searchTextUsed'", fontWeight = FontWeight.Bold)
                    if (foundToShow.isNotEmpty()) {
                        VerticalScrollColumn {
                            foundToShow.forEach {
                                SmallStoryCard(it, onClick = { selectedStories = selectedStories + (it.id to it) })
                            }
                        }
                    }
                }
            }
        }
    }
}