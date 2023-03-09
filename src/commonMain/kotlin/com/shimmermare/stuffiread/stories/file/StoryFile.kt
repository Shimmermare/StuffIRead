package com.shimmermare.stuffiread.stories.file

/**
 * File with story content.
 * Usually there is single file per story, but user may choose to save file in multiple formats
 * and sometimes stories are published in multiple files and user chooses not to merge them.
 */
data class StoryFile(
    val meta: StoryFileMeta,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoryFile

        if (meta != other.meta) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = meta.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

    companion object {
        // Match words consisting of any unicode letter characters
        private val WORD_REGEX = Regex("[\\p{L}-`']+")

        private val ILLEGAL_FILE_NAME_CHARS = Regex("[^a-zA-Z0-9+_. -]")

        fun createFileName(name: String, format: StoryFileFormat): String {
            return replaceIllegalCharsInFileName(name).let {
                if (format.extension != null && !it.endsWith(".${format.extension}")) {
                    "$it.${format.extension}"
                } else {
                    it
                }
            }
        }

        fun replaceIllegalCharsInFileName(name: String): String {
            return name.replace(ILLEGAL_FILE_NAME_CHARS, "_").trim()
        }

        fun fromText(name: String, text: String): StoryFile {
            val bytes = text.toByteArray(charset = Charsets.UTF_8)
            val info = StoryFileMeta(
                fileName = createFileName(name, StoryFileFormat.TXT),
                format = StoryFileFormat.TXT,
                originalName = name,
                wordCount = countWords(text),
                size = bytes.size.toUInt()
            )
            return StoryFile(
                meta = info,
                content = bytes
            )
        }

        fun countWords(text: String): UInt {
            return WORD_REGEX.findAll(text).count().toUInt()
        }
    }
}