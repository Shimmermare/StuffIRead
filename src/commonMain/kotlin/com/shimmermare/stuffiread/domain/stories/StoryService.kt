package com.shimmermare.stuffiread.domain.stories

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.data.StoryDatabase
import com.shimmermare.stuffiread.data.stories.StoryDatasource

class StoryService(
    storyDatabase: StoryDatabase
) {
    private val database: Database = storyDatabase.database
    private val storyDatasource: StoryDatasource = storyDatabase.storyDatasource

    fun getAllStories(): List<Story> {
        return database.transactionWithResult {
            storyDatasource.findAll()
        }
    }
}