package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
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
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.InputForm
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantRangeFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalRangeFormField
import com.shimmermare.stuffiread.ui.components.form.RangedOptionalIntFormField
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.input.OutlinedEnumField
import com.shimmermare.stuffiread.ui.components.input.OutlinedUIntField
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.search.DefaultSearchBarModifier
import com.shimmermare.stuffiread.ui.components.search.SearchBar
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.ASCENDING_UNKNOWN_FIRST
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.DESCENDING
import com.shimmermare.stuffiread.ui.components.story.SortBehavior.DESCENDING_UNKNOWN_FIRST
import com.shimmermare.stuffiread.ui.components.tag.MultiTagPicker
import com.shimmermare.stuffiread.ui.util.TimeUtils
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
    val state = remember(currentFilter) { InputFormState(currentFilter) }
    var showAdvanced: Boolean by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.widthIn(min = 200.dp, max = 600.dp).weight(1F, false)
            ) {
                SearchBar(
                    searchText = state.data.nameContains ?: "",
                    placeholderText = "Search by name",
                    onSearchTextChanged = { state.data = state.data.copy(nameContains = it.ifBlank { null }) },
                    modifier = DefaultSearchBarModifier.onKeyEvent {
                        if (it.key == Key.Enter && state.isValid && currentFilter != state.data) {
                            onFilterChange(state.data)
                            true
                        } else {
                            false
                        }
                    },
                    onClearClick = { state.data = state.data.copy(nameContains = null) },
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.requiredWidth(IntrinsicSize.Max),
            ) {
                Button(
                    onClick = { showAdvanced = !showAdvanced },
                ) {
                    Text(if (showAdvanced) "Hide advanced" else "Show advanced")
                }
                Button(
                    onClick = { onFilterChange(StoryFilter.DEFAULT) },
                    enabled = state.data != StoryFilter.DEFAULT,
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = { if (state.isValid && currentFilter != state.data) onFilterChange(state.data) },
                    enabled = state.isValid && currentFilter != state.data
                ) {
                    Text("Search")
                }
            }
        }
        if (showAdvanced) {
            AdvancedStoryFilterControls(state)
        }
    }
}

@Composable
private fun AdvancedStoryFilterControls(state: InputFormState<StoryFilter>) {
    Box(
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        InputForm(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        ) { formState ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(600.dp),
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                this.advancedStoryFilterFields(formState)
            }
        }
    }
}

private fun LazyGridScope.advancedStoryFilterFields(state: InputFormState<StoryFilter>) {
    advancedControlItem(Arrangement.spacedBy(20.dp)) {
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
        TextFormField(
            id = "contentContains",
            state = state,
            name = "Content contains",
            getter = { it.contentContains ?: "" },
            setter = { data, value -> data.copy(contentContains = value.ifBlank { null }) },
            singleLine = false
        )
    }
    advancedControlItem(Arrangement.spacedBy(10.dp)) {
        TextFormField(
            id = "urlContains",
            state = state,
            name = "URL",
            getter = { it.urlContains ?: "" },
            setter = { data, value -> data.copy(urlContains = value.ifBlank { null }) },
        )
        OptionalInstantRangeFormField(
            id = "publishedRange",
            state = state,
            name = "Published date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.publishedAfter },
            toGetter = { it.publishedBefore },
            setter = { data, from, to -> data.copy(publishedAfter = from, publishedBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "changedRange",
            state = state,
            name = "Changed date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.changedAfter },
            toGetter = { it.changedBefore },
            setter = { data, from, to -> data.copy(changedAfter = from, changedBefore = to) },
        )
    }
    advancedControlItem(Arrangement.spacedBy(15.dp)) {
        FormField(
            id = "tagsPresent",
            state = state,
            name = "With tags",
            getter = { it.tagsPresent ?: emptySet() },
            setter = { form, value -> form.copy(tagsPresent = value.ifEmpty { null }) },
        ) { value, _, onValueChange ->
            MultiTagPicker(
                title = "Pick tags that should be present (including implied)",
                pickedTagIds = value,
                onPick = onValueChange
            )
        }
        FormField(
            id = "tagsAbsent",
            state = state,
            name = "Without tags",
            getter = { it.tagsAbsent ?: emptySet() },
            setter = { form, value -> form.copy(tagsAbsent = value.ifEmpty { null }) },
        ) { value, _, onValueChange ->
            MultiTagPicker(
                title = "Pick tags that should be absent (including implied)",
                pickedTagIds = value,
                onPick = onValueChange
            )
        }
        OptionalRangeFormField(
            id = "wordCountRange",
            state = state,
            name = "Word count range (inclusive)",
            defaultValue = { 0u },
            fromGetter = { it.wordCountGreaterOrEqual },
            toGetter = { it.wordCountLessOrEqual },
            setter = { data, from, to -> data.copy(wordCountGreaterOrEqual = from, wordCountLessOrEqual = to) }
        ) { value, valid, onValueChange ->
            OutlinedUIntField(
                value = value,
                isError = !valid,
                modifier = Modifier.widthIn(max = 120.dp).height(36.dp),
                onValueChange = onValueChange
            )
        }
    }
    advancedControlItem(Arrangement.spacedBy(20.dp)) {
        OptionalRangeFormField(
            id = "scoreRange",
            state = state,
            name = "Score range (inclusive)",
            defaultValue = { Score(0F) },
            fromGetter = { it.scoreGreaterOrEqual },
            toGetter = { it.scoreLessOrEqual },
            setter = { data, from, to -> data.copy(scoreGreaterOrEqual = from, scoreLessOrEqual = to) }
        ) { value, _, onValueChange ->
            StoryScoreInput(value, onValueChange)
        }
        TextFormField(
            id = "reviewContains",
            state = state,
            name = "Review contains",
            getter = { it.reviewContains ?: "" },
            setter = { data, value -> data.copy(reviewContains = value.ifBlank { null }) },
            singleLine = false
        )
    }
    advancedControlItem(Arrangement.spacedBy(10.dp)) {
        OptionalInstantRangeFormField(
            id = "firstReadRange",
            state = state,
            name = "First read date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.firstReadAfter },
            toGetter = { it.firstReadBefore },
            setter = { data, from, to -> data.copy(firstReadAfter = from, firstReadBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "lastReadRange",
            state = state,
            name = "First read date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.lastReadAfter },
            toGetter = { it.lastReadBefore },
            setter = { data, from, to -> data.copy(lastReadAfter = from, lastReadBefore = to) },
        )
        OptionalRangeFormField(
            id = "timesReadRange",
            state = state,
            name = "Times read range (inclusive)",
            defaultValue = { 0u },
            fromGetter = { it.timesReadGreaterOrEqual },
            toGetter = { it.timesReadLessOrEqual },
            setter = { data, from, to -> data.copy(timesReadGreaterOrEqual = from, timesReadLessOrEqual = to) }
        ) { value, valid, onValueChange ->
            OutlinedUIntField(
                value = value,
                isError = !valid,
                modifier = Modifier.widthIn(max = 80.dp).height(36.dp),
                onValueChange = onValueChange
            )
        }
    }
    advancedControlItem(Arrangement.spacedBy(10.dp)) {
        OptionalInstantRangeFormField(
            id = "createdRange",
            state = state,
            name = "Created date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.createdAfter },
            toGetter = { it.createdBefore },
            setter = { data, from, to -> data.copy(createdAfter = from, createdBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "updatedRange",
            state = state,
            name = "Created date range (inclusive)",
            defaultValue = { TimeUtils.instantAtTodayMidnight() },
            fromGetter = { it.updatedAfter },
            toGetter = { it.updatedBefore },
            setter = { data, from, to -> data.copy(updatedAfter = from, updatedBefore = to) },
        )
        RangedOptionalIntFormField(
            id = "id",
            state = state,
            name = "ID",
            defaultValue = 1,
            getter = { it.idIn?.firstOrNull()?.value?.toInt() },
            setter = { data, value -> data.copy(idIn = value?.let { setOf(StoryId(it.toUInt())) }) },
            range = 1..Int.MAX_VALUE,
        )
    }
}

private fun LazyGridScope.advancedControlItem(
    verticalArrangement: Arrangement.Vertical,
    content: @Composable () -> Unit
) {
    item {
        Box(
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                verticalArrangement = verticalArrangement,
                modifier = Modifier.widthIn(max = 800.dp)
            ) {
                content()
            }
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