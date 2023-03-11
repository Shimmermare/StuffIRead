package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.tags.TagId

interface TagMappingService {
    /**
     * Mapping is case-insensitive, and name will be trimmed from blank characters.
     *
     * @return map of tags that were successfully mapped. Tag name is unchanged from [names].
     */
    fun mapTags(names: Iterable<String>): Map<String, TagId>

    /**
     * Save valid mappings. Tag name will be saved in lowercase and trimmed from blank chars.
     */
    fun updateMappings(mappings: Map<String, TagId>)
}