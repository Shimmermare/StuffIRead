package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.ImportSettings

data class PastebinImportSettings(
    val pasteKeys: List<PasteKey>,
) : ImportSettings {
    init {
        require(pasteKeys.isNotEmpty()) {
            "No paste keys provided"
        }
        require(pasteKeys.toSet().size == pasteKeys.size) {
            "Duplicate paste keys: ${pasteKeys - pasteKeys.toSet()}"
        }
    }
}
