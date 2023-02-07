package com.shimmermare.stuffiread.ui

import com.shimmermare.stuffiread.data.StoryDatabase
import com.shimmermare.stuffiread.domain.stories.StoryService
import com.shimmermare.stuffiread.domain.tags.TagCategoryService
import com.shimmermare.stuffiread.domain.tags.TagService
import io.github.aakira.napier.Napier
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

class AppState(
    private val dbFile: Path,
    createIfNotExist: Boolean
) : AutoCloseable {
    private val storyDatabase: StoryDatabase

    val storyService: StoryService
    val tagCategoryService: TagCategoryService
    val tagService: TagService

    init {
        if (dbFile.extension != STORY_DB_FILE_EXT) {
            throw IllegalArgumentException("File $dbFile has wrong extension (expected .$STORY_DB_FILE_EXT)")
        }
        if (!createIfNotExist && !Files.exists(dbFile)) {
            throw IllegalArgumentException("File $dbFile doesn't exist")
        }

        Napier.i { "Opening story database '$dbFile'" }
        try {
            storyDatabase = StoryDatabase(dbFile)
            storyService = StoryService(storyDatabase)
            tagCategoryService = TagCategoryService(storyDatabase)
            tagService = TagService(storyDatabase)
        } catch (e: Exception) {
            Napier.e(e) { "Failed to open story database '$dbFile'" }
            throw e
        }
    }

    override fun close() {
        Napier.i { "Closing story database: '$dbFile'" }
        storyDatabase.close()
    }

    companion object {
        const val STORY_DB_FILE_EXT = "stories"
    }
}

