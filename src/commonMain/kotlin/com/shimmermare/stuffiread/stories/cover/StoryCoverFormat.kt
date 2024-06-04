package com.shimmermare.stuffiread.stories.cover

enum class StoryCoverFormat(
    val mimeSubType: String,
    vararg val extensions: String
) {
    PNG("png", "png"),
    JPEG("jpeg", "jpg", "jpeg"),
    WEBP("webp", "webp"),
    BMP("bmp", "bmp"),
    GIF("gif", "gif"),
    HEIF("avif", "heif", "avif"),
    ICO("vnd.microsoft.icon", "ico"),
    WBMP("vnd.wap.wbmp", "wbmp");

    init {
        require(extensions.isNotEmpty()) {
            "Extension is not specified for $name"
        }
    }

    companion object {
        val VALUES = values().toSet()
        val ALL_EXTENSIONS = VALUES.flatMap { it.extensions.toList() }.toSet()

        private val FORMAT_BY_EXTENSION = values().flatMap { format -> format.extensions.map { it to format } }.toMap()
        private val FORMAT_BY_MIME_SUBTYPE = values().associateBy { it.mimeSubType }

        fun getByExtension(extension: String): StoryCoverFormat? = FORMAT_BY_EXTENSION[extension.lowercase()]
        fun getByMimeSubType(mimeSubType: String): StoryCoverFormat? = FORMAT_BY_MIME_SUBTYPE[mimeSubType.lowercase()]
    }
}