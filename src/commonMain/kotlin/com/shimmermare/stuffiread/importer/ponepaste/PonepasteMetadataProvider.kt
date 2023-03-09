package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

expect object PonepasteMetadataProvider {
    suspend fun get(pasteId: PonepasteId): PasteMetadata<PonepasteId>
}