package com.shimmermare.stuffiread.stories.file

import com.shimmermare.stuffiread.stories.FileBasedStoryService
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.util.AppJson
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes
import kotlin.io.path.useDirectoryEntries
import kotlin.io.path.writeBytes

@OptIn(ExperimentalPathApi::class, ExperimentalSerializationApi::class)
class StoryFilesServiceImpl(
    archiveDirectory: Path,
) : StoryFilesService {
    private val storiesDirectory = archiveDirectory.resolve(FileBasedStoryService.STORIES_DIR_NAME)

    override suspend fun getStoryFilesMeta(storyId: StoryId): List<StoryFileMeta> {
        val filesDir = getFilesDir(storyId)
        return withContext(Dispatchers.IO) {
            if (filesDir.notExists()) return@withContext emptyList()

            filesDir.useDirectoryEntries { entries ->
                entries.filter { it.extension != "json" }.map { contentFile ->
                    try {
                        readFileMeta(storyId, contentFile.name)
                    } catch (e: Exception) {
                        Napier.e(e) { "Failed to read story file meta storyId=$storyId fileName='${contentFile.name}'" }
                        throw e
                    }
                }.toList()
            }
        }
    }

    override suspend fun getStoryFiles(storyId: StoryId): List<StoryFile> {
        val filesDir = getFilesDir(storyId)
        return withContext(Dispatchers.IO) {
            if (filesDir.notExists()) return@withContext emptyList()

            filesDir.useDirectoryEntries { entries ->
                entries.filter { it.extension != "json" }.map { contentFile ->
                    try {
                        readFile(storyId, contentFile.name)
                    } catch (e: Exception) {
                        Napier.e(e) { "Failed to read story file storyId=$storyId fileName='${contentFile.name}'" }
                        throw e
                    }
                }.toList()
            }
        }
    }

    override suspend fun updateStoryFiles(storyId: StoryId, files: List<StoryFile>) {
        val filesDir = getFilesDir(storyId)
        withContext(Dispatchers.IO) {
            Napier.i { "Updating story files storyId=$storyId files=${files.map { it.meta.fileName }}" }

            if (filesDir.notExists()) filesDir.createDirectories()

            val filesToUpdate = files.map { it.meta.fileName }.toSet()
            filesDir.useDirectoryEntries { entries ->
                entries.filter { it.extension != "json" }.forEach { contentFile ->
                    if (!filesToUpdate.contains(contentFile.name)) {
                        contentFile.deleteIfExists()
                        contentFile.resolveSibling("${contentFile.name}.json").deleteIfExists()
                        Napier.i { "Deleted '$contentFile' and meta file" }
                    }
                }
            }

            files.forEach { file ->
                try {
                    writeFile(storyId, file)
                } catch (e: Exception) {
                    Napier.e(e) { "Failed to write story file storyId=$storyId fileName='${file.meta.fileName}'" }
                    throw e
                }
            }
        }
    }

    private fun readFile(storyId: StoryId, fileName: String): StoryFile {
        val filesDir = getFilesDir(storyId)
        val contentFile = filesDir.resolve(fileName)
        val metaFile = filesDir.resolve("$fileName.json")

        val meta = readFileMeta(contentFile, metaFile)
        return StoryFile(meta = meta, content = contentFile.readBytes())
    }

    private fun readFileMeta(storyId: StoryId, fileName: String): StoryFileMeta {
        val filesDir = getFilesDir(storyId)
        return readFileMeta(
            contentFile = filesDir.resolve(fileName),
            metaFile = filesDir.resolve("$fileName.json")
        )
    }

    private fun readFileMeta(contentFile: Path, metaFile: Path): StoryFileMeta {
        val storedMeta = metaFile.inputStream().use { AppJson.decodeFromStream<StoredStoryFileMeta>(it) }
        val fileSize = contentFile.fileSize()

        return StoryFileMeta(
            fileName = contentFile.name,
            format = storedMeta.format,
            originalName = storedMeta.originalName,
            wordCount = storedMeta.wordCount,
            size = fileSize.toUInt()
        )
    }

    private fun writeFile(storyId: StoryId, file: StoryFile) {
        val filesDir = getFilesDir(storyId)
        val metaFile = filesDir.resolve("${file.meta.fileName}.json")
        val contentFile = filesDir.resolve(file.meta.fileName)

        metaFile.outputStream().use {
            AppJson.encodeToStream(
                value = StoredStoryFileMeta(
                    format = file.meta.format,
                    originalName = file.meta.originalName,
                    wordCount = file.meta.wordCount
                ),
                stream = it
            )
        }
        contentFile.writeBytes(file.content)
    }

    private fun getFilesDir(storyId: StoryId): Path {
        return storiesDirectory.resolve(Path(storyId.toString(), STORY_FILES_DIR_NAME))
    }

    companion object {
        private const val STORY_FILES_DIR_NAME = "files"
    }

    /**
     * Stored representation of [StoryFileMeta].
     * [StoryFileMeta.fileName] and [StoryFileMeta.size] will be added from actual file.
     */
    @Serializable
    private data class StoredStoryFileMeta(
        val format: StoryFileFormat,
        val originalName: String,
        val wordCount: UInt,
    )
}