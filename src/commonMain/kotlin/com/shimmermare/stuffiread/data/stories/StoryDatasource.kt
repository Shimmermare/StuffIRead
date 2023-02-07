package com.shimmermare.stuffiread.data.stories

import com.shimmermare.stuffiread.domain.stories.Story

interface StoryDatasource {
    fun findAll(): List<Story>
}