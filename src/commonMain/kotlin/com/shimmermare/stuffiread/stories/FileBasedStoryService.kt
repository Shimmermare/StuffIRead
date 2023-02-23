package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.stories.FileBasedStoryService.Companion.STORIES_DIR_NAME
import com.shimmermare.stuffiread.util.AppJson
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
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

    override suspend fun getStoriesByIds(storyIds: Collection<StoryId>, ignoreInvalid: Boolean): List<Story> {
        return withContext(Dispatchers.IO) {
            storyIds.map { storyId ->
                async {
                    try {
                        readStory(storyId)
                    } catch (e: Exception) {
                        if (ignoreInvalid) {
                            Napier.e(e) { "Ignoring invalid story file" }
                            null
                        } else {
                            throw Exception("Failed to load story $storyId", e)
                        }
                    }
                }
            }.mapNotNull { it.await() }
        }
    }

    override suspend fun getAllStories(ignoreInvalid: Boolean): List<Story> {
        return withContext(Dispatchers.IO) {
            if (storiesDirectory.notExists()) return@withContext emptyList()
            storiesDirectory.listDirectoryEntries()
                .map { storyDir ->
                    async {
                        try {
                            readStory(storyDir.resolve(STORY_FILE_NAME))
                        } catch (e: Exception) {
                            if (ignoreInvalid) {
                                Napier.e(e) { "Ignoring invalid story file" }
                                null
                            } else {
                                throw Exception("Failed to load story ${storyDir.fileName}", e)
                            }
                        }
                    }
                }
                .mapNotNull { it.await() }
        }
    }

    override suspend fun createStory(story: Story): Story {
        require(story.id != 0u) {
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
            if (storiesDirectory.notExists()) storiesDirectory.createDirectories()
            writeStory(story)
            return@withContext story
        }
    }

    private fun readStory(storyId: StoryId): Story? {
        val storyFile = storiesDirectory.resolve(storyId.toString()).resolve(STORY_FILE_NAME)
        if (storyFile.notExists()) return null

        val story = storyFile.inputStream(StandardOpenOption.READ).use { Json.decodeFromStream<Story>(it) }
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
        val story = storyFile.inputStream(StandardOpenOption.READ).use { Json.decodeFromStream<Story>(it) }
        require(story.id == storyId) {
            "Story ID in file (${story.id}) doesn't match location ($storyId)"
        }
        return story
    }

    private fun writeStory(story: Story) {
        require(story.id != 0u) {
            "0 story ID is not allowed"
        }
        val storyFile = storiesDirectory.resolve(story.id.toString()).resolve(STORY_FILE_NAME)
        storyFile.outputStream(
            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        ).use {
            AppJson.encodeToStream(story, it)
        }
    }

    private fun nextFreeId(): StoryId {
        val entries = storiesDirectory.listDirectoryEntries()
        if (entries.isEmpty()) return 1u
        return entries.mapNotNull { it.fileName.toString().toUIntOrNull() }.max() + 1u
    }

    companion object {
        private const val STORIES_DIR_NAME = "stories"
        private const val STORY_FILE_NAME = "story.json"
    }
}