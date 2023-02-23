package com.shimmermare.stuffiread.stories

class StorySearchServiceImpl(
    private val storyService: StoryService
) : StorySearchService {
    override suspend fun getStoriesByFilter(filter: StoryFilter): List<Story> {
        var stories = if (filter.idIn != null) {
            storyService.getStoriesByIds(filter.idIn)
        } else {
            storyService.getAllStories()
        }

        if (filter.nameContains != null) {
            stories = stories.filter { it.name.contains(filter.nameContains, ignoreCase = true) }
        }

        if (filter.tagsPresent != null) {
            // TODO: Add filtering by ids
        }

        return stories
    }
}