package com.shimmermare.stuffiread.stories.cover

import com.shimmermare.stuffiread.stories.FileBasedStoryService
import com.shimmermare.stuffiread.stories.StoryId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.extension
import kotlin.io.path.notExists
import kotlin.io.path.readBytes
import kotlin.io.path.useDirectoryEntries
import kotlin.io.path.writeBytes

class StoryCoverServiceImpl(
    archiveDirectory: Path
) : StoryCoverService {
    private val storiesDirectory = archiveDirectory.resolve(FileBasedStoryService.STORIES_DIR_NAME)

    override suspend fun getStoryCover(storyId: StoryId): StoryCover? {
        val storyDir = getStoryDir(storyId)
        return withContext(Dispatchers.IO) {
            if (storyDir.notExists()) return@withContext null

            val coverFile = storyDir.useDirectoryEntries("$COVER_FILE_NAME_WITHOUT_EXTENSION.*") { entries ->
                entries.filter { StoryCoverFormat.ALL_EXTENSIONS.contains(it.extension.lowercase()) }.firstOrNull()
            } ?: return@withContext null

            StoryCover(
                format = StoryCoverFormat.getByExtension(coverFile.extension)
                    ?: error("Unknown format: ${coverFile.extension}"), data = coverFile.readBytes()
            )
        }
    }

    override suspend fun updateStoryCover(storyId: StoryId, cover: StoryCover?) {
        val storyDir = getStoryDir(storyId)
        return withContext(Dispatchers.IO) {
            if (storyDir.notExists()) storyDir.createDirectories()

            storyDir.useDirectoryEntries("$COVER_FILE_NAME_WITHOUT_EXTENSION.*") { entries ->
                entries.forEach { existingCoverFile ->
                    existingCoverFile.deleteIfExists()
                }
            }

            if (cover != null) {
                storyDir.resolve("$COVER_FILE_NAME_WITHOUT_EXTENSION.${cover.format.extension.first()}")
                    .writeBytes(cover.data)
            }
        }
    }

    override suspend fun loadCoverFile(path: Path): StoryCover {
        return withContext(Dispatchers.IO) {
            val format = StoryCoverFormat.getByExtension(path.extension)
                ?: error("Unsupported cover format: ${path.extension}")
            StoryCover(
                format = format, data = path.readBytes()
            )
        }
    }

    private fun getStoryDir(storyId: StoryId): Path {
        return storiesDirectory.resolve(storyId.toString())
    }

    companion object {
        private const val COVER_FILE_NAME_WITHOUT_EXTENSION = "cover"
    }
}