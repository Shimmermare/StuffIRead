package com.shimmermare.stuffiread.importer.ponebin

import com.shimmermare.stuffiread.importer.pastebased.PasteBasedStoryImporter
import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

object PonebinImporter : PasteBasedStoryImporter<PasteKey>(
    descriptionPrefix = "Imported from poneb.in"
) {
    override suspend fun requestMetadata(pasteId: PasteKey): PasteMetadata<PasteKey> {
        return PonebinMetadataProvider.get(pasteId)
    }

    override fun getPasteUrl(pasteId: PasteKey): String {
        return "https://poneb.in/$pasteId"
    }

    override fun getRawUrl(pasteId: PasteKey): String {
        return "https://poneb.in/raw/${pasteId.value}"
    }
}