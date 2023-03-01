package com.shimmermare.stuffiread.stories.file

enum class StoryFileFormat(val extension: String?) {
    TXT("txt"),
    EPUB("epub"),
    PDF("pdf"),
    HTML("html"),
    OTHER(null);

    companion object {
        private val formatsByExtension = values().associateBy { it.extension }

        fun getByExtension(extension: String): StoryFileFormat {
            return formatsByExtension.getOrDefault(extension, OTHER)
        }
    }
}