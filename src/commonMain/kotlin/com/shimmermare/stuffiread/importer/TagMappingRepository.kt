package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.tags.TagId

/**
 * Mappings allowed to point to non-existing tags.
 */
interface TagMappingRepository {
    suspend fun loadMappings(): Map<String, TagId>

    suspend fun saveMappings(mappings: Map<String, TagId>)
}