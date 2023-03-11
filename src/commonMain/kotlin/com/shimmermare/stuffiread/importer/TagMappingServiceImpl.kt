package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

class TagMappingServiceImpl(
    private val tagMappingRepository: TagMappingRepository,
    private val tagService: TagService,
) : TagMappingService {

    private val currentMappings: MutableMap<String, TagId>

    init {
        currentMappings = runBlocking {
            tagMappingRepository.loadMappings()
                .filter { (_, tagId) -> tagService.tagExistsById(tagId) }
                .toMap(ConcurrentHashMap())
        }
    }

    override fun mapTags(names: Iterable<String>): Map<String, TagId> {
        val tagsById = tagService.getTags().associateBy { it.id }
        val toMap = names.associateByTo(hashMapOf()) { normalizeTagName(it) }
        val mapped = hashMapOf<String, TagId>()

        // Find existing mappings
        toMap.entries.removeIf { (normalizedName, name) ->
            val existingMapping = currentMappings[normalizedName]
            if (existingMapping != null && tagsById.containsKey(existingMapping)) {
                mapped[name] = existingMapping
                true
            } else {
                false
            }
        }

        if (toMap.isNotEmpty()) {
            val tagsByNormalizedName = tagsById.mapKeys { (_, tag) -> normalizeTagName(tag.name.value) }
            // Try to map by name
            toMap.entries.removeIf { (normalizedName, name) ->
                val tag = tagsByNormalizedName[normalizedName]
                if (tag != null) {
                    mapped[name] = tag.id
                    true
                } else {
                    false
                }
            }
        }

        if (mapped.isNotEmpty()) {
            Napier.i { "Mapped ${mapped.size} tags: $mapped" }
        }
        if (toMap.isNotEmpty()) {
            Napier.i { "Failed to map ${toMap.size} tags: ${toMap.values}" }
        }

        return mapped
    }

    override fun updateMappings(mappings: Map<String, TagId>) {
        val normalized = mappings
            .filter { (_, tagId) -> tagService.tagExistsById(tagId) }
            .mapKeys { (name, _) -> normalizeTagName(name) }
        if (normalized.isEmpty()) return
        currentMappings.putAll(normalized)
        saveCurrentMappingsAsync()
        Napier.i { "Saved tag mappings: $normalized" }
    }

    private fun normalizeTagName(name: String): String {
        return name.trim().lowercase()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveCurrentMappingsAsync() {
        GlobalScope.launch {
            try {
                val toSave = currentMappings.filter { (_, tagId) -> tagService.tagExistsById(tagId) }
                tagMappingRepository.saveMappings(toSave)
            } catch (e: Exception) {
                Napier.e(e) { "Failed to save tag mappings" }
            }
        }
    }
}