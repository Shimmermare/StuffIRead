package com.shimmermare.stuffiread

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

class StoryDatabase(
    file: Path? = null
) {
    var status: Status by mutableStateOf(Status.LOADING)
        private set

    var currentFile: Path? by mutableStateOf(file)
        private set

    init {
        initDb(false)
    }

    fun open(file: Path, createIfNotExist: Boolean = false) {
        if (file.extension != STORY_DB_FILE_EXT) {
            throw IllegalArgumentException("File $file has wrong extension (expected .$STORY_DB_FILE_EXT)")
        }
        if (!createIfNotExist && !Files.exists(file)) {
            throw IllegalArgumentException("File $file doesn't exist")
        }
        currentFile = file
        initDb(createIfNotExist)
    }

    private fun initDb(createIfNotExist: Boolean) {
        if (currentFile == null) {
            status = Status.DB_FILE_NOT_SET
            return
        }
        if (!createIfNotExist && !Files.exists(currentFile!!)) {
            status = Status.DB_FILE_MISSING
            return
        }

        status = Status.LOADING
    }

    companion object {
        const val STORY_DB_FILE_EXT = "stories"
    }

    enum class Status(
        val isError: Boolean,
    ) {
        LOADING(false),
        DB_FILE_NOT_SET(true),
        DB_FILE_MISSING(true),
        OK(false),
    }
}