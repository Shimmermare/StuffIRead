package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.tags.TagId

data class StoryFilter(
    val idIn: Set<StoryId>? = null,
    val nameContains: String? = null,
    /**
     * Includes both explicit and implied tags.
     */
    val tagsPresent: Set<TagId>? = null,
)
