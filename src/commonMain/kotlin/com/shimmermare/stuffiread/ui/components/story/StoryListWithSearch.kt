package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.OptionalFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalIntFormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollContainer
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector
import com.shimmermare.stuffiread.ui.util.LoadingContainer
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@Composable
fun StoryListWithSearch(
    app: AppState,
) {
    var filter: StoryFilter by remember { mutableStateOf(StoryFilter.DEFAULT) }
    var ignoreInvalidStories: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StoryFilterControls(app, filter, onFilterChange = { filter = it })

        Divider(modifier = Modifier.fillMaxWidth())

        LoadingContainer(
            key = filter to ignoreInvalidStories,
            timeout = 120.seconds,
            loader = { (filter, ignoreInvalidStories) ->
                app.storyArchive!!.storySearchService.getStoriesByFilter(filter, ignoreInvalidStories)
            },
            onError = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ErrorCard(
                        title = "Search failed",
                        exception = it,
                        suggestion = "You can exclude invalid stories from search."
                    )
                    Button(onClick = { ignoreInvalidStories = true }) {
                        Text("Ignore invalid stories")
                    }
                }
            }
        ) { storiesFlow ->
            val stories: MutableList<Story> = remember { mutableStateListOf() }

            LaunchedEffect(Unit) {
                storiesFlow.collect { story ->
                    stories.add(story)
                }
            }

            val storiesFoundText = when {
                stories.isEmpty() -> "No stories found"
                stories.size == 1 -> "Found 1 story"
                else -> "Found ${stories.size} stories"
            }
            Text(storiesFoundText, style = MaterialTheme.typography.h5)

            val gridState = rememberLazyGridState()
            LazyVerticalGrid(
                state = gridState,
                modifier = Modifier.fillMaxWidth().heightIn(max = 10000.dp),
                columns = GridCells.Adaptive(minSize = 1000.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            ) {

                itemsIndexed(stories) { index, story ->
                    StoryCard(app, story, abs(index - gridState.firstVisibleItemIndex) < 10)
                }
            }
        }
    }
}

@Composable
private fun StoryFilterControls(app: AppState, currentFilter: StoryFilter, onFilterChange: (StoryFilter) -> Unit) {
    var filter: StoryFilter by remember(currentFilter) { mutableStateOf(currentFilter) }
    var showAdvanced: Boolean by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.width(600.dp)
            ) {
                SearchBar(
                    searchText = filter.nameContains ?: "",
                    placeholderText = "Search by name",
                    onSearchTextChanged = { filter = filter.copy(nameContains = it.ifBlank { null }) },
                    onClearClick = { filter = filter.copy(nameContains = null) },
                )
            }

            Spacer(modifier = Modifier.weight(1F))

            Button(
                onClick = { showAdvanced = !showAdvanced }
            ) {
                Text(if (showAdvanced) "Hide advanced" else "Show advanced")
            }
            Button(
                onClick = { onFilterChange(StoryFilter.DEFAULT) },
                enabled = filter != StoryFilter.DEFAULT,
            ) {
                Text("Reset")
            }
            Button(
                onClick = { if (currentFilter != filter) onFilterChange(filter) },
                enabled = currentFilter != filter
            ) {
                Text("Search")
            }
        }
        if (showAdvanced) {
            AdvancedStoryFilterControls(app, filter) { filter = it }
        }
    }
}

@Composable
private fun AdvancedStoryFilterControls(app: AppState, filter: StoryFilter, onFilterChange: (StoryFilter) -> Unit) {
    val state = remember(filter) { InputFormState(filter) }

    LaunchedEffect(state.data) {
        if (filter != state.data) {
            onFilterChange(state.data)
        }
    }
    Box(
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        VerticalScrollContainer {
            Row {
                InputForm(
                    state = state,
                    modifier = Modifier.width(800.dp),
                ) { formState ->
                    AdvancedStoryFilterFields(app, formState)
                }
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun AdvancedStoryFilterFields(app: AppState, state: InputFormState<StoryFilter>) {
    OptionalIntFormField(
        id = "id",
        state = state,
        name = "ID",
        defaultValue = 1,
        getter = { it.idIn?.firstOrNull()?.toInt() },
        setter = { data, value -> data.copy(idIn = value?.let { setOf(it.toUInt()) }) },
        range = 1..Int.MAX_VALUE,
    )
    TextFormField(
        id = "authorContains",
        state = state,
        name = "Author",
        getter = { it.authorContains ?: "" },
        setter = { data, value -> data.copy(authorContains = value.ifBlank { null }) }
    )
    TextFormField(
        id = "descriptionContains",
        state = state,
        name = "Description contains",
        getter = { it.descriptionContains ?: "" },
        setter = { data, value -> data.copy(descriptionContains = value.ifBlank { null }) },
        singleLine = false
    )
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "publishedAfter",
                state = state,
                name = "Published after",
                getter = { it.publishedAfter },
                setter = { data, value -> data.copy(publishedAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "publishedBefore",
                state = state,
                name = "Published before",
                getter = { it.publishedBefore },
                setter = { data, value -> data.copy(publishedBefore = value) }
            )
        }
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "changedAfter",
                state = state,
                name = "Changed after",
                getter = { it.changedAfter },
                setter = { data, value -> data.copy(changedAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "changedBefore",
                state = state,
                name = "Changed before",
                getter = { it.changedBefore },
                setter = { data, value -> data.copy(changedBefore = value) }
            )
        }
    }
    OptionalFormField(
        id = "tags",
        state = state,
        name = "Tags",
        description = "Including implied tags",
        defaultValue = { emptySet() },
        getter = { it.tagsPresent },
        setter = { form, value -> form.copy(tagsPresent = value) },
    ) { value, _, onValueChange ->
        MultiTagSelector(
            tagService = app.storyArchive!!.tagService,
            selectedIds = value,
            onSelect = onValueChange
        )
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalFormField(
                id = "scoreGreaterOrEqual",
                state = state,
                name = "Score greater than or equal",
                defaultValue = { Score(0F) },
                getter = { it.scoreGreaterOrEqual },
                setter = { data, value -> data.copy(scoreGreaterOrEqual = value) },
            ) { value, _, onValueChange ->
                StoryScoreInput(app, value, onValueChange)
            }
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalFormField(
                id = "scoreLessOrEqual",
                state = state,
                name = "Score less than or equal",
                defaultValue = { Score(1F) },
                getter = { it.scoreLessOrEqual },
                setter = { data, value -> data.copy(scoreLessOrEqual = value) },
            ) { value, _, onValueChange ->
                StoryScoreInput(app, value, onValueChange)
            }
        }
    }
    TextFormField(
        id = "reviewContains",
        state = state,
        name = "Review contains",
        getter = { it.reviewContains ?: "" },
        setter = { data, value -> data.copy(reviewContains = value.ifBlank { null }) },
        singleLine = false
    )
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "firstReadAfter",
                state = state,
                name = "First read after",
                getter = { it.firstReadAfter },
                setter = { data, value -> data.copy(firstReadAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "firstReadBefore",
                state = state,
                name = "First read before",
                getter = { it.firstReadBefore },
                setter = { data, value -> data.copy(firstReadBefore = value) }
            )
        }
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "lastReadAfter",
                state = state,
                name = "Last read after",
                getter = { it.lastReadAfter },
                setter = { data, value -> data.copy(lastReadAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "lastReadBefore",
                state = state,
                name = "Last read before",
                getter = { it.lastReadBefore },
                setter = { data, value -> data.copy(lastReadBefore = value) }
            )
        }
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalIntFormField(
                id = "timesReadGreaterOrEqual",
                state = state,
                name = "Times read greater than or equal",
                defaultValue = 0,
                getter = { it.timesReadGreaterOrEqual },
                setter = { data, value -> data.copy(timesReadGreaterOrEqual = value) },
                range = 0..Int.MAX_VALUE,
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalIntFormField(
                id = "timesReadLessOrEqual",
                state = state,
                name = "Times read less than or equal",
                defaultValue = 5,
                getter = { it.timesReadLessOrEqual },
                setter = { data, value -> data.copy(timesReadLessOrEqual = value) },
                range = 0..Int.MAX_VALUE,
            )
        }
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "createdAfter",
                state = state,
                name = "Created after",
                getter = { it.createdAfter },
                setter = { data, value -> data.copy(createdAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "createdBefore",
                state = state,
                name = "Created before",
                getter = { it.createdBefore },
                setter = { data, value -> data.copy(createdBefore = value) }
            )
        }
    }
    Row {
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "updatedAfter",
                state = state,
                name = "Updated after",
                getter = { it.updatedAfter },
                setter = { data, value -> data.copy(updatedAfter = value) }
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalInstantFormField(
                id = "updatedBefore",
                state = state,
                name = "Updated before",
                getter = { it.updatedBefore },
                setter = { data, value -> data.copy(updatedBefore = value) }
            )
        }
    }
}