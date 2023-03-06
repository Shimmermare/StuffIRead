package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.pastebased.PasteBasedStoryImporter
import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

object PonepasteImporter : PasteBasedStoryImporter<PonepasteId>(
    descriptionPrefix = "Imported from ponepaste.org"
) {
    override suspend fun requestMetadata(pasteId: PonepasteId): PasteMetadata<PonepasteId> {
        return PonepasteMetadataProvider.get(pasteId)
    }

    override fun getPasteUrl(pasteId: PonepasteId): String {
        return "https://ponepaste.org/$pasteId"
    }

    override fun getRawUrl(pasteId: PonepasteId): String {
        return "https://ponepaste.org/raw/$pasteId"
    }
}