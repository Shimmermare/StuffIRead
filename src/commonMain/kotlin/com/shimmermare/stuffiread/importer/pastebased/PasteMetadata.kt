package com.shimmermare.stuffiread.importer.pastebased

import kotlinx.datetime.Instant

data class PasteMetadata<PasteId>(
    val id: PasteId,
    val author: String,
    val name: String,
    val addedDate: Instant?,
    val modifiedDate: Instant? = addedDate,
    val tags: Set<String> = emptySet()
)