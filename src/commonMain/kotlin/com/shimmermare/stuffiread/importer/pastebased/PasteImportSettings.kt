package com.shimmermare.stuffiread.importer.pastebased

import com.shimmermare.stuffiread.importer.ImportSettings

data class PasteImportSettings<PasteId>(
    /**
     * Unique paste IDs. Order will be preserved in imported story.
     */
    val pasteIds: List<PasteId>
) : ImportSettings {
    init {
        require(pasteIds.isNotEmpty()) {
            "No paste IDs provided"
        }
        require(pasteIds.toSet().size == pasteIds.size) {
            "Duplicate paste IDs: ${pasteIds - pasteIds.toSet()}"
        }
    }
}