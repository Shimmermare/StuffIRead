@file:UseSerializers(TagSerializer::class, TagCategorySerializer::class)

package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.util.AppJson
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

@OptIn(ExperimentalSerializationApi::class)
class FileBasedTagTreeService(
    private val archiveDirectory: Path,
) : TagTreeService {
    private val tagsFilePath = archiveDirectory.resolve(TAGS_FILE_NAME)

    private val fileLock = Mutex()

    override suspend fun getTree(): TagTree {
        return withContext(Dispatchers.IO) {
            try {
                fileLock.withLock {
                    if (tagsFilePath.notExists()) {
                        return@withContext TagTree()
                    }

                    val storedTree = tagsFilePath.inputStream(StandardOpenOption.READ).use {
                        Json.decodeFromStream(StoredTagTreeSerializer, it)
                    }

                    TagTree(storedTree.categories, storedTree.tags)
                }
            } catch (e: Exception) {
                throw Exception("Failed to read tag tree", e)
            }
        }
    }

    override suspend fun updateTree(tree: TagTree) {
        withContext(Dispatchers.IO) {
            try {
                fileLock.withLock {
                    if (archiveDirectory.notExists()) archiveDirectory.createDirectories()

                    tagsFilePath.outputStream(
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.CREATE
                    ).use {
                        val storedTagTree = StoredTagTree(tree.categories, tree.tags)
                        AppJson.encodeToStream(storedTagTree, it)
                    }
                }
            } catch (e: Exception) {
                throw Exception("Failed to update tag tree", e)
            }
        }
    }

    companion object {
        private const val TAGS_FILE_NAME = "tags.json"
    }

    @Serializable
    private data class StoredTagTree(
        val categories: Collection<TagCategory> = emptyList(),
        val tags: Collection<Tag> = emptyList()
    ) {
        companion object {
            const val VERSION: UInt = 1u
        }
    }

    /**
     * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
     * Replace with [Serializer] when fixed.
     */
    private object StoredTagTreeSerializer : JsonVersionedSerializer<StoredTagTree>(
        currentVersion = StoredTagTree.VERSION,
        migrations = listOf(
            // Example:
            // Migration(1u) {
            //     JsonObject(it.jsonObject + ("newProperty" to JsonPrimitive("new value")))
            // }
        ),
        actualSerializer = StoredTagTree.serializer()
    )
}