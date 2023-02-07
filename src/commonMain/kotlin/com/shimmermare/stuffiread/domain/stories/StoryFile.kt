package com.shimmermare.stuffiread.domain.stories

typealias StoryFileId = Int

/**
 * FIle with contents of story.
 * Usually there is 1 file per 1 story, but user may choose to save file in multiple formats.
 * Also, sometimes stories are published in multiple files and user chooses not to merge them.
 */
data class StoryFile(
    val id: StoryFileId = 0,
    /**
     * Story contents of this file contains.
     */
    val story: StoryId,
    /**
     * Story file format.
     */
    val format: StoryFileFormat,
    /**
     * Unique file name of story in archive folder.
     */
    val fileName: String,
    val sha256: String,
    val wordCount: Int,
    val size: Int,
) {
}

enum class StoryFileFormat {
    TXT,
    EPUB,
    PDF,
    HTML,
    OTHER,
}