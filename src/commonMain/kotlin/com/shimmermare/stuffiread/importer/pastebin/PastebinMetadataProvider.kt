package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

expect object PastebinMetadataProvider {
    suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey>
}