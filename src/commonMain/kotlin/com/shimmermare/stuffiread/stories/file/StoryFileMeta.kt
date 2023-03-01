package com.shimmermare.stuffiread.stories.file

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Info about story file.
 */
@Serializable
data class StoryFileMeta(
    /**
     * Unique file name in story's context.
     * Usually same as [originalName] but with extension and illegal characters removed.
     */
    val fileName: String,
    val format: StoryFileFormat,
    val originalName: String,
    val added: Instant,
    val wordCount: UInt,
    val size: UInt,
)