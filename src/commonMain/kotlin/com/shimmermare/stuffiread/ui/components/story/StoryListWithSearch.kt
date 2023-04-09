package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryFilter
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.ui.CurrentLocale
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
import com.shimmermare.stuffiread.ui.components.tag.MultiTagPicker
import com.shimmermare.stuffiread.ui.util.TimeUtils
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
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
    var showFilters: Boolean by remember { mutableStateOf(false) }

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
                    placeholderText = Strings.page_stories_search_nameSearchbarPlaceholder.remember(),
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
                    onClick = { showFilters = !showFilters },
                ) {
                    Text(
                        if (showFilters)
                            Strings.page_stories_search_hideAdvancedFiltersButton.remember()
                        else
                            Strings.page_stories_search_showAdvancedFiltersButton.remember()
                    )
                }
                Button(
                    onClick = { onFilterChange(StoryFilter.DEFAULT) },
                    enabled = state.data != StoryFilter.DEFAULT,
                ) {
                    Text(Strings.page_stories_search_resetButton.remember())
                }
                Button(
                    onClick = { if (state.isValid && currentFilter != state.data) onFilterChange(state.data) },
                    enabled = state.isValid && currentFilter != state.data
                ) {
                    Text(Strings.page_stories_search_searchButton.remember())
                }
            }
        }

        if (showFilters) {
            Box(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                InputForm(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                ) { formState ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(800.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalArrangement = Arrangement.spacedBy(40.dp)
                    ) {
                        this.filterFields(formState)
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.filterFields(state: InputFormState<StoryFilter>) {
    filterItem(Arrangement.spacedBy(20.dp)) {
        TextFormField(
            id = "authorContains",
            state = state,
            name = Strings.page_stories_search_filter_authorContains.remember(),
            getter = { it.authorContains ?: "" },
            setter = { data, value -> data.copy(authorContains = value.ifBlank { null }) }
        )
        TextFormField(
            id = "descriptionContains",
            state = state,
            name = Strings.page_stories_search_filter_descriptionContains.remember(),
            getter = { it.descriptionContains ?: "" },
            setter = { data, value -> data.copy(descriptionContains = value.ifBlank { null }) },
            singleLine = false
        )
        TextFormField(
            id = "contentContains",
            state = state,
            name = Strings.page_stories_search_filter_contentContains.remember(),
            getter = { it.contentContains ?: "" },
            setter = { data, value -> data.copy(contentContains = value.ifBlank { null }) },
            singleLine = false
        )
    }
    filterItem(Arrangement.spacedBy(10.dp)) {
        TextFormField(
            id = "urlContains",
            state = state,
            name = Strings.page_stories_search_filter_url.remember(),
            getter = { it.urlContains ?: "" },
            setter = { data, value -> data.copy(urlContains = value.ifBlank { null }) },
        )
        OptionalInstantRangeFormField(
            id = "publishedRange",
            state = state,
            name = Strings.page_stories_search_filter_publishedRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.publishedAfter },
            toGetter = { it.publishedBefore },
            setter = { data, from, to -> data.copy(publishedAfter = from, publishedBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "changedRange",
            state = state,
            name = Strings.page_stories_search_filter_changedRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.changedAfter },
            toGetter = { it.changedBefore },
            setter = { data, from, to -> data.copy(changedAfter = from, changedBefore = to) },
        )
    }
    filterItem(Arrangement.spacedBy(15.dp)) {
        FormField(
            id = "tagsPresent",
            state = state,
            name = Strings.page_stories_search_filter_tagsPresent.remember(),
            getter = { it.tagsPresent ?: emptySet() },
            setter = { form, value -> form.copy(tagsPresent = value.ifEmpty { null }) },
        ) { value, _, onValueChange ->
            MultiTagPicker(
                title = Strings.page_stories_search_filter_tagsPresent_pickerTitle.remember(),
                pickedTagIds = value,
                onPick = onValueChange
            )
        }
        FormField(
            id = "tagsAbsent",
            state = state,
            name = Strings.page_stories_search_filter_tagsAbsent.remember(),
            getter = { it.tagsAbsent ?: emptySet() },
            setter = { form, value -> form.copy(tagsAbsent = value.ifEmpty { null }) },
        ) { value, _, onValueChange ->
            MultiTagPicker(
                title = Strings.page_stories_search_filter_tagsAbsent_pickerTitle.remember(),
                pickedTagIds = value,
                onPick = onValueChange
            )
        }
        OptionalRangeFormField(
            id = "wordCountRange",
            state = state,
            name = Strings.page_stories_search_filter_wordCountRange.remember(),
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
    filterItem(Arrangement.spacedBy(20.dp)) {
        OptionalRangeFormField(
            id = "scoreRange",
            state = state,
            name = Strings.page_stories_search_filter_scoreRange.remember(),
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
            name = Strings.page_stories_search_filter_reviewContains.remember(),
            getter = { it.reviewContains ?: "" },
            setter = { data, value -> data.copy(reviewContains = value.ifBlank { null }) },
            singleLine = false
        )
    }
    filterItem(Arrangement.spacedBy(10.dp)) {
        OptionalInstantRangeFormField(
            id = "firstReadRange",
            state = state,
            name = Strings.page_stories_search_filter_firstReadRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.firstReadAfter },
            toGetter = { it.firstReadBefore },
            setter = { data, from, to -> data.copy(firstReadAfter = from, firstReadBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "lastReadRange",
            state = state,
            name = Strings.page_stories_search_filter_lastReadRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.lastReadAfter },
            toGetter = { it.lastReadBefore },
            setter = { data, from, to -> data.copy(lastReadAfter = from, lastReadBefore = to) },
        )
        OptionalRangeFormField(
            id = "timesReadRange",
            state = state,
            name = Strings.page_stories_search_filter_timesReadRange.remember(),
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
    filterItem(Arrangement.spacedBy(10.dp)) {
        OptionalInstantRangeFormField(
            id = "createdRange",
            state = state,
            name = Strings.page_stories_search_filter_createdRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.createdAfter },
            toGetter = { it.createdBefore },
            setter = { data, from, to -> data.copy(createdAfter = from, createdBefore = to) },
        )
        OptionalInstantRangeFormField(
            id = "updatedRange",
            state = state,
            name = Strings.page_stories_search_filter_updatedRange.remember(),
            defaultValue = { TimeUtils.instantTodayAt0000() },
            fromGetter = { it.updatedAfter },
            toGetter = { it.updatedBefore },
            setter = { data, from, to -> data.copy(updatedAfter = from, updatedBefore = to) },
        )
        RangedOptionalIntFormField(
            id = "id",
            state = state,
            name = Strings.page_stories_search_filter_id.remember(),
            defaultValue = 1,
            getter = { it.idIn?.firstOrNull()?.value?.toInt() },
            setter = { data, value -> data.copy(idIn = value?.let { setOf(StoryId(it.toUInt())) }) },
            range = 1..Int.MAX_VALUE,
        )
    }
}

private fun LazyGridScope.filterItem(
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
    var sortOrder: SortOrder by remember { mutableStateOf(SortOrder.DEFAULT) }
    val comparator: Comparator<Story> = remember(sortBy, sortOrder) { buildComparator(sortBy, sortOrder) }

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
                title = Strings.page_stories_search_searchFailed_title.toString(),
                exception = e,
                suggestion = Strings.page_stories_search_searchFailed_suggestion.toString()
            )
            stories.clear()
        }
        inProgress = false
    }

    LaunchedEffect(sortBy, sortOrder) {
        stories.sortWith(comparator)
    }

    if (error != null) {
        VerticalScrollColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ErrorCard(error!!)
            Button(onClick = { ignoreInvalidStories = true }) {
                Text(Strings.page_stories_search_searchFailed_ignoreInvalidButton.remember())
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (stories.isNotEmpty() || !inProgress) {
                    Text(stories_search_result.remember(stories.size), style = MaterialTheme.typography.h6)
                }
                if (inProgress) {
                    Text(Strings.page_stories_search_searching.remember(), style = MaterialTheme.typography.h6)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(Strings.page_stories_search_sort_sortBy.remember())
                OutlinedEnumField(
                    value = sortBy,
                    allowedValues = SortBy.values,
                    displayNameProvider = sortByDisplayNameProvider(),
                    onValueChange = { sortBy = it },
                    inputFieldModifier = Modifier.width(300.dp).height(36.dp)
                )
                OutlinedEnumField(
                    value = sortOrder,
                    allowedValues = SortOrder.values,
                    displayNameProvider = sortOrderDisplayNameProvider(),
                    onValueChange = { sortOrder = it },
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

@Composable
private fun sortByDisplayNameProvider(): (SortBy) -> String {
    val map = remember(CurrentLocale) {
        hashMapOf(
            SortBy.ID to Strings.page_stories_search_sort_sortBy_ID.toString(),
            SortBy.NAME to Strings.page_stories_search_sort_sortBy_NAME.toString(),
            SortBy.AUTHOR to Strings.page_stories_search_sort_sortBy_AUTHOR.toString(),
            SortBy.PUBLISHED to Strings.page_stories_search_sort_sortBy_PUBLISHED.toString(),
            SortBy.CHANGED to Strings.page_stories_search_sort_sortBy_CHANGED.toString(),
            SortBy.SCORE to Strings.page_stories_search_sort_sortBy_SCORE.toString(),
            SortBy.FIRST_READ to Strings.page_stories_search_sort_sortBy_FIRST_READ.toString(),
            SortBy.LAST_READ to Strings.page_stories_search_sort_sortBy_LAST_READ.toString(),
            SortBy.TIMES_READ to Strings.page_stories_search_sort_sortBy_TIMES_READ.toString(),
            SortBy.CREATED to Strings.page_stories_search_sort_sortBy_CREATED.toString(),
            SortBy.UPDATED to Strings.page_stories_search_sort_sortBy_UPDATED.toString(),
        )
    }
    return { map.getOrDefault(it, it.name) }
}

@Composable
private fun sortOrderDisplayNameProvider(): (SortOrder) -> String {
    val map = remember(CurrentLocale) {
        hashMapOf(
            SortOrder.ASCENDING to Strings.page_stories_search_sort_sortOrder_ASCENDING.toString(),
            SortOrder.DESCENDING to Strings.page_stories_search_sort_sortOrder_DESCENDING.toString(),
            SortOrder.ASCENDING_UNKNOWN_FIRST to Strings.page_stories_search_sort_sortOrder_ASCENDING_UNKNOWN_FIRST.toString(),
            SortOrder.DESCENDING_UNKNOWN_FIRST to Strings.page_stories_search_sort_sortOrder_DESCENDING_UNKNOWN_FIRST.toString(),
        )
    }
    return { map.getOrDefault(it, it.name) }
}

private fun buildComparator(sortBy: SortBy, behavior: SortOrder): Comparator<Story> {
    return Comparator { a, b ->
        val aUnknown = sortBy.isUnknown(a)
        val bUnknown = sortBy.isUnknown(b)

        val unknownSorted = aUnknown.compareTo(bUnknown)
        if (unknownSorted != 0) {
            return@Comparator if (behavior == SortOrder.ASCENDING_UNKNOWN_FIRST || behavior == SortOrder.DESCENDING_UNKNOWN_FIRST) {
                unknownSorted * -1
            } else {
                unknownSorted
            }
        }

        val byValue = sortBy.comparator.compare(a, b)
        return@Comparator if (behavior == SortOrder.DESCENDING || behavior == SortOrder.DESCENDING_UNKNOWN_FIRST) {
            byValue * -1
        } else {
            byValue
        }
    }
}

private enum class SortBy(
    val comparator: Comparator<Story>,
    val isUnknown: (Story) -> Boolean = { false }
) {
    ID(Comparator.comparing { it.id }),
    NAME(Comparator.comparing { it.name }),
    AUTHOR(Comparator.comparing { it.author }, isUnknown = { !it.author.isPresent }),
    PUBLISHED(Comparator.comparing { it.published ?: DISTANT_FUTURE }, isUnknown = { it.published == null }),
    CHANGED(Comparator.comparing { it.changed ?: DISTANT_FUTURE }, isUnknown = { it.changed == null }),
    SCORE(Comparator.comparing { it.score ?: Score(1F) }, isUnknown = { it.score == null }),
    FIRST_READ(Comparator.comparing { it.firstRead ?: DISTANT_FUTURE }, isUnknown = { it.firstRead == null }),
    LAST_READ(Comparator.comparing { it.lastRead ?: DISTANT_FUTURE }, isUnknown = { it.lastRead == null }),
    TIMES_READ(Comparator.comparing { it.reads.size }),
    CREATED(Comparator.comparing { it.created }),
    UPDATED(Comparator.comparing { it.updated });

    companion object {
        val DEFAULT = UPDATED

        // To avoid alloc
        val values: Set<SortBy> = values().toSet()
    }
}

private enum class SortOrder {
    ASCENDING,
    DESCENDING,
    ASCENDING_UNKNOWN_FIRST,
    DESCENDING_UNKNOWN_FIRST;

    companion object {
        val DEFAULT = DESCENDING

        // To avoid alloc
        val values: Set<SortOrder> = values().toSet()
    }
}

private val stories_search_result = PluralLocalizedString(
    Strings.page_stories_search_result_zero,
    Strings.page_stories_search_result_one,
    Strings.page_stories_search_result_two,
    Strings.page_stories_search_result_few,
    Strings.page_stories_search_result_many,
    Strings.page_stories_search_result_other,
)