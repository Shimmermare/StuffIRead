package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.pastebased.PasteBasedStoryImporter
import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

object PastebinImporter : PasteBasedStoryImporter<PasteKey>(
    descriptionPrefix = "Imported from Pastebin"
) {
    override suspend fun requestMetadata(pasteId: PasteKey): PasteMetadata<PasteKey> {
        return PastebinMetadataProvider.get(pasteId)
    }

    override fun getPasteUrl(pasteId: PasteKey): String {
        return "https://pastebin.com/$pasteId"
    }

    override fun getRawUrl(pasteId: PasteKey): String {
        return "https://pastebin.com/raw/${pasteId.value}"
    }
}