package com.shimmermare.stuffiread.stories

import kotlinx.coroutines.flow.Flow

interface StorySearchService {
    suspend fun getStoriesByFilter(filter: StoryFilter, ignoreInvalidStories: Boolean = false): Flow<Story>
}