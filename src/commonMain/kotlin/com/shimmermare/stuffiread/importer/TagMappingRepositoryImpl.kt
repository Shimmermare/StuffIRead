package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.util.AppJson
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

@OptIn(ExperimentalSerializationApi::class)
class TagMappingRepositoryImpl(private val archiveDirectory: Path) : TagMappingRepository {

    private val mappingsFile = archiveDirectory.resolve(MAPPINGS_FILE_NAME)

    private val fileLock = Mutex()

    override suspend fun loadMappings(): Map<String, TagId> {
        return withContext(Dispatchers.IO) {
            try {
                fileLock.withLock {
                    if (mappingsFile.notExists()) return@withContext emptyMap()

                    mappingsFile.inputStream()
                        .use { AppJson.decodeFromStream(StoredTagMappingsSerializer, it).mappings }
                        .also { Napier.i { "Loaded ${it.size} tag mappings from file: $it" } }
                }
            } catch (e: Exception) {
                throw Exception("Failed to load tag mappings", e)
            }
        }
    }

    override suspend fun saveMappings(mappings: Map<String, TagId>) {
        withContext(Dispatchers.IO) {
            try {
                fileLock.withLock {
                    if (archiveDirectory.notExists()) archiveDirectory.createDirectories()

                    mappingsFile.outputStream().use {
                        AppJson.encodeToStream(StoredTagMappingsSerializer, SavedTagMappings(mappings), it)
                    }
                    Napier.i { "Saved ${mappings.size} tag mappings to file: $mappings" }
                }
            } catch (e: Exception) {
                throw Exception("Failed to save tag mappings", e)
            }
        }
    }

    companion object {
        private const val MAPPINGS_FILE_NAME = "tag_mappings.json"
    }
}


@Serializable
private data class SavedTagMappings(
    val mappings: Map<String, TagId>
) {
    companion object {
        const val VERSION = 1u
    }
}

/**
 * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
 * Replace with [Serializer] when fixed.
 */
private object StoredTagMappingsSerializer : JsonVersionedSerializer<SavedTagMappings>(
    currentVersion = SavedTagMappings.VERSION,
    migrations = listOf(
        // Example:
        // Migration(1u) {
        //     JsonObject(it.jsonObject + ("newProperty" to JsonPrimitive("new value")))
        // }
    ),
    actualSerializer = SavedTagMappings.serializer()
)