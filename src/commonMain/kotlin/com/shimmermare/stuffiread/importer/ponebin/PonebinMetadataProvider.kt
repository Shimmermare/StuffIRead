package com.shimmermare.stuffiread.importer.ponebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

expect object PonebinMetadataProvider {
    suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey>
}