package com.shimmermare.stuffiread.stories.cover

enum class StoryCoverFormat(
    vararg val extension: String
) {
    PNG("png"),
    JPEG("jpg", "jpeg"),
    WEBP("webp"),
    BMP("bmp"),
    GIF("gif"),
    HEIF("heif", "avif"),
    ICO("ico"),
    WBMP("wbmp");

    init {
        require(extension.isNotEmpty()) {
            "Extension is not specified for $name"
        }
    }

    companion object {
        val VALUES = values().toSet()
        val ALL_EXTENSIONS = VALUES.flatMap { it.extension.toList() }.toSet()

        private val FORMAT_BY_EXTENSION = values().flatMap { format -> format.extension.map { it to format } }.toMap()

        fun getByExtension(extension: String): StoryCoverFormat? = FORMAT_BY_EXTENSION[extension.lowercase()]
    }
}