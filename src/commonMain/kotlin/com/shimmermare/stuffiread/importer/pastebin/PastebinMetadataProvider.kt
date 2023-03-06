package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

/**
 * Paste metadata (e.g. author) information is not accessible via public API. So we have to just parse HTTP.
 * TODO: Find better way to access this lol.
 *
 * Because relying on HTML structure inevitably will lead to problems later (when pastebin changes something),
 * the only "required" field is paste name. Other fields will be replaced with placeholders if failed.
 */
expect object PastebinMetadataProvider {
    suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey>
}