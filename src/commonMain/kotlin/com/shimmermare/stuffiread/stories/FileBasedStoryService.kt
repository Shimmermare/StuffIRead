package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.FileBasedStoryService.Companion.STORIES_DIR_NAME
import com.shimmermare.stuffiread.util.AppJson
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Implementation that stores stories in individual subdirectories in [archiveDirectory]/[STORIES_DIR_NAME].
 * Story directory name is the same as its ID.
 */
@OptIn(ExperimentalSerializationApi::class)
class FileBasedStoryService(
    private val archiveDirectory: Path,
) : StoryService {
    private val storiesDirectory = archiveDirectory.resolve(STORIES_DIR_NAME)

    override suspend fun getStoryById(storyId: StoryId): Story? {
        return withContext(Dispatchers.IO) {
            readStory(storyId)
        }
    }

    override suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean): Flow<Story> {
        return withContext(Dispatchers.IO) {
            flow {
                val flowCollector = this
                storyIds.map { storyId ->
                    try {
                        val story = readStory(storyId)
                        if (story != null) flowCollector.emit(story)
                    } catch (e: Exception) {
                        if (ignoreInvalid) {
                            Napier.e(e) { "Ignoring invalid story file" }
                        } else {
                            throw Exception("Failed to load story $storyId", e)
                        }
                    }
                }
            }
        }
    }

    override suspend fun getAllStories(ignoreInvalid: Boolean): Flow<Story> {
        return withContext(Dispatchers.IO) {
            if (storiesDirectory.notExists()) return@withContext emptyFlow()

            flow {
                val flowCollector = this
                storiesDirectory.listDirectoryEntries().map { storyDir ->
                    try {
                        val story = readStory(storyDir.resolve(STORY_FILE_NAME))
                        if (story != null) flowCollector.emit(story)
                    } catch (e: Exception) {
                        if (ignoreInvalid) {
                            Napier.e(e) { "Ignoring invalid story file" }
                        } else {
                            throw Exception("Failed to load story ${storyDir.fileName}", e)
                        }
                    }
                }
            }
        }
    }

    override suspend fun createStory(story: Story): Story {
        require(story.id == 0u) {
            "Story can't be created with predefined ID (${story.id})"
        }
        return withContext(Dispatchers.IO) {
            if (storiesDirectory.notExists()) storiesDirectory.createDirectories()

            val created = story.copy(id = nextFreeId())
            writeStory(created)
            return@withContext created
        }
    }

    override suspend fun updateStory(story: Story): Story {
        return withContext(Dispatchers.IO) {
            require(storyFilePath(story.id).exists()) {
                "Can't update non-existing story ${story.id}"
            }
            writeStory(story)
            return@withContext story
        }
    }

    @OptIn(ExperimentalPathApi::class)
    override suspend fun deleteStoryById(storyId: StoryId) {
        withContext(Dispatchers.IO) {
            val storyDirectory = storiesDirectory.resolve(storyId.toString())
            storyDirectory.walk(PathWalkOption.INCLUDE_DIRECTORIES)
                .sortedDescending()
                .forEach(Path::deleteIfExists)
        }
    }

    private fun readStory(storyId: StoryId): Story? {
        val storyFile = storyFilePath(storyId)
        if (storyFile.notExists()) return null

        val story = storyFile.inputStream().use { Json.decodeFromStream<Story>(it) }
        require(story.id == storyId) {
            "Story ID in file (${story.id}) doesn't match location ($storyId)"
        }
        return story
    }

    private fun readStory(storyFile: Path): Story? {
        if (storyFile.notExists()) return null

        val storyId: StoryId = try {
            storyFile.parent.fileName.toString().toUInt()
        } catch (e: NumberFormatException) {
            error("Story folder name is not valid ID: '${storyFile.parent.fileName}'")
        }
        val story = storyFile.inputStream().use { Json.decodeFromStream<Story>(it) }
        require(story.id == storyId) {
            "Story ID in file (${story.id}) doesn't match location ($storyId)"
        }
        return story
    }

    private fun writeStory(story: Story) {
        require(story.id != 0u) {
            "0 story ID is not allowed"
        }
        val storyDir = storiesDirectory.resolve(story.id.toString())
        if (storyDir.notExists()) storyDir.createDirectories()

        storyDir.resolve(STORY_FILE_NAME).outputStream().use {
            AppJson.encodeToStream(story, it)
        }
    }

    private fun nextFreeId(): StoryId {
        val entries = storiesDirectory.listDirectoryEntries()
        if (entries.isEmpty()) return 1u
        return entries.mapNotNull { it.fileName.toString().toUIntOrNull() }.max() + 1u
    }

    private fun storyFilePath(storyId: StoryId): Path {
        return storiesDirectory.resolve(Path(storyId.toString(), STORY_FILE_NAME))
    }

    companion object {
        const val STORIES_DIR_NAME = "stories"
        private const val STORY_FILE_NAME = "story.json"
    }
}