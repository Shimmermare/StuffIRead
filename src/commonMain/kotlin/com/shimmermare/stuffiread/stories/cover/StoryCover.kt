package com.shimmermare.stuffiread.stories.cover

data class StoryCover(
    val format: StoryCoverFormat,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoryCover

        if (format != other.format) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = format.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
