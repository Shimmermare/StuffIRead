package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storySearchService
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.components.error.ErrorInfo
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.OptionalFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalUIntFormField
import com.shimmermare.stuffiread.ui.components.form.RangedOptionalIntFormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.input.OutlinedEnumField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.search.DefaultSearchBarModifier
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.ASCENDING_UNKNOWN_FIRST
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.DESCENDING
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.DESCENDING_UNKNOWN_FIRST
import com.shimmermare.stuffiread.ui.components.tag.MultiTagPicker
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant.Companion.DISTANT_FUTURE
import kotlin.math.abs

@Composable
fun StoryListWithSearch(presetFilter: StoryFilter = StoryFilter.DEFAULT) {
    var currentFilter: StoryFilter by remember(presetFilter) { mutableStateOf(presetFilter) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StoryFilterControls(currentFilter, onFilterChange = { currentFilter = it })
        Divider(modifier = Modifier.fillMaxWidth())
        StoryList(currentFilter)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun StoryFilterControls(currentFilter: StoryFilter, onFilterChange: (StoryFilter) -> Unit) {
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
                    modifier = DefaultSearchBarModifier.onKeyEvent {
                        if (it.key == Key.Enter && currentFilter != filter) {
                            onFilterChange(filter)
                            true
                        } else {
                            false
                        }
                    },
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
            AdvancedStoryFilterControls(filter) { filter = it }
        }
    }
}

@Composable
private fun AdvancedStoryFilterControls(filter: StoryFilter, onFilterChange: (StoryFilter) -> Unit) {
    val state = remember(filter) { InputFormState(filter) }

    LaunchedEffect(state.data) {
        if (filter != state.data) {
            onFilterChange(state.data)
        }
    }
    Box(
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        VerticalScrollColumn {
            Row {
                InputForm(
                    state = state,
                    modifier = Modifier.width(800.dp),
                ) { formState ->
                    AdvancedStoryFilterFields(formState)
                }
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun AdvancedStoryFilterFields(state: InputFormState<StoryFilter>) {
    RangedOptionalIntFormField(
        id = "id",
        state = state,
        name = "ID",
        defaultValue = 1,
        getter = { it.idIn?.firstOrNull()?.value?.toInt() },
        setter = { data, value -> data.copy(idIn = value?.let { setOf(StoryId(it.toUInt())) }) },
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
        MultiTagPicker(
            title = "Pick tags to search for",
            pickedTagIds = value,
            onPick = onValueChange
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
                StoryScoreInput(value, onValueChange)
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
                StoryScoreInput(value, onValueChange)
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
            OptionalUIntFormField(
                id = "timesReadGreaterOrEqual",
                state = state,
                name = "Times read greater than or equal",
                getter = { it.timesReadGreaterOrEqual },
                setter = { data, value -> data.copy(timesReadGreaterOrEqual = value) },
            )
        }
        Row(modifier = Modifier.weight(1F)) {
            OptionalUIntFormField(
                id = "timesReadLessOrEqual",
                state = state,
                name = "Times read less than or equal",
                defaultValue = 5u,
                getter = { it.timesReadLessOrEqual },
                setter = { data, value -> data.copy(timesReadLessOrEqual = value) },
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

@Composable
private fun StoryList(filter: StoryFilter) {
    var sortBy: SortBy by remember { mutableStateOf(SortBy.DEFAULT) }
    var sortBehavior: SortBehavior by remember { mutableStateOf(SortBehavior.DEFAULT) }
    val comparator: Comparator<Story> = remember(sortBy, sortBehavior) { buildComparator(sortBy, sortBehavior) }

    var ignoreInvalidStories: Boolean by remember { mutableStateOf(false) }

    val stories: MutableList<Story> = remember(filter) { mutableStateListOf() }

    var inProgress: Boolean by remember { mutableStateOf(false) }
    var error: ErrorInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(filter, ignoreInvalidStories) {
        inProgress = true
        error = null
        try {
            storySearchService.getStoriesByFilter(filter, ignoreInvalidStories)
                .onEach { story ->
                    stories.add(story)
                    stories.sortWith(comparator)
                }
                .collect()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to search stories with filter $filter" }
            error = ErrorInfo(
                title = "Search failed",
                exception = e,
                suggestion = "You can exclude invalid stories from search."
            )
            stories.clear()
        }
        inProgress = false
    }

    LaunchedEffect(sortBy, sortBehavior) {
        stories.sortWith(comparator)
    }

    if (error != null) {
        VerticalScrollColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            ErrorCard(error!!)
            Button(onClick = { ignoreInvalidStories = true }) {
                Text("Ignore invalid stories")
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val storiesFoundText = when {
                inProgress -> when {
                    stories.isEmpty() -> "Searching..."
                    stories.size == 1 -> "Found 1 story. Searching for more..."
                    else -> "Found ${stories.size} stories. Searching for more..."
                }

                else -> when {
                    stories.isEmpty() -> "No stories found"
                    stories.size == 1 -> "Found 1 story"
                    else -> "Found ${stories.size} stories"
                }
            }
            Text(storiesFoundText, style = MaterialTheme.typography.h5)

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedEnumField(
                    value = sortBy,
                    allowedValues = SortBy.values,
                    displayNameProvider = { "Sort by: " + it.displayName },
                    onValueChange = { sortBy = it },
                    inputFieldModifier = Modifier.width(300.dp).height(36.dp)
                )
                OutlinedEnumField(
                    value = sortBehavior,
                    allowedValues = SortBehavior.values,
                    displayNameProvider = { it.displayName },
                    onValueChange = { sortBehavior = it },
                    inputFieldModifier = Modifier.width(250.dp).height(36.dp)
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())

        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.fillMaxWidth().heightIn(max = 10000.dp),
            columns = GridCells.Adaptive(minSize = 1000.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        ) {
            itemsIndexed(stories) { index, story ->
                StoryCard(story, visible = abs(index - gridState.firstVisibleItemIndex) < 10)
            }
        }
    }
}

private fun buildComparator(sortBy: SortBy, behavior: SortBehavior): Comparator<Story> {
    return Comparator { a, b ->
        val aUnknown = sortBy.isUnknown(a)
        val bUnknown = sortBy.isUnknown(b)

        val unknownSorted = aUnknown.compareTo(bUnknown)
        if (unknownSorted != 0) {
            return@Comparator if (behavior == ASCENDING_UNKNOWN_FIRST || behavior == DESCENDING_UNKNOWN_FIRST) {
                unknownSorted * -1
            } else {
                unknownSorted
            }
        }

        val byValue = sortBy.comparator.compare(a, b)
        return@Comparator if (behavior == DESCENDING || behavior == DESCENDING_UNKNOWN_FIRST) {
            byValue * -1
        } else {
            byValue
        }
    }
}

@Suppress("unused")
private enum class SortBy(
    val displayName: String,
    val isUnknown: (Story) -> Boolean = { false },
    val comparator: Comparator<Story>
) {
    ID("ID", comparator = Comparator.comparing { it.id }),
    NAME("Name", comparator = Comparator.comparing { it.name }),
    AUTHOR("Author", isUnknown = { !it.author.isPresent }, comparator = Comparator.comparing { it.author }),
    PUBLISHED(
        "Published",
        isUnknown = { it.published == null },
        comparator = Comparator.comparing { it.published ?: DISTANT_FUTURE }
    ),
    CHANGED(
        "Changed",
        isUnknown = { it.changed == null },
        comparator = Comparator.comparing { it.changed ?: DISTANT_FUTURE }
    ),
    SCORE(
        "Score",
        isUnknown = { it.score == null },
        comparator = Comparator.comparing { it.score ?: Score(1F) }
    ),
    FIRST_READ(
        "First read",
        isUnknown = { it.firstRead == null },
        comparator = Comparator.comparing { it.firstRead ?: DISTANT_FUTURE }
    ),
    LAST_READ(
        "Last read",
        isUnknown = { it.lastRead == null },
        comparator = Comparator.comparing { it.lastRead ?: DISTANT_FUTURE }
    ),
    TIMES_READ(
        "Times read",
        comparator = Comparator.comparing { it.timesRead }
    ),
    CREATED(
        "Created",
        comparator = Comparator.comparing { it.created }
    ),
    UPDATED(
        "Updated",
        comparator = Comparator.comparing { it.updated }
    );

    companion object {
        val DEFAULT = NAME

        // To avoid alloc
        val values: Set<SortBy> = values().toSet()
    }
}

private enum class SortBehavior(
    val displayName: String,
) {
    ASCENDING("Ascending"),
    DESCENDING("Descending"),
    ASCENDING_UNKNOWN_FIRST("Ascending (unknown first)"),
    DESCENDING_UNKNOWN_FIRST("Descending (unknown first)");

    companion object {
        val DEFAULT = ASCENDING

        // To avoid alloc
        val values: Set<SortBehavior> = values().toSet()
    }
}