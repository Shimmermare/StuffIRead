package com.shimmermare.stuffiread.stories

interface StorySearchService {
    suspend fun getStoriesByFilter(filter: StoryFilter): List<Story>
}