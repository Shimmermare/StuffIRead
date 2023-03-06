package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

/**
 * Same problem as Pastebin - ponepaste has no API to get meta, so we have to parse HTML.
 *
 * Because relying on HTML structure inevitably will lead to problems later,
 * the only "required" field is paste name. Other fields will be replaced with placeholders if failed.
 */
expect object PonepasteMetadataProvider {
    suspend fun get(pasteId: PonepasteId): PasteMetadata<PonepasteId>
}