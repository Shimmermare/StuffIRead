package com.shimmermare.stuffiread.importer.ponebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata

/**
 * Same problem as Pastebin - poneb.in has no API to get meta, so we have to parse HTML.
 *
 * Because relying on HTML structure inevitably will lead to problems later,
 * the only "required" field is paste name. Other fields will be replaced with placeholders if failed.
 */
expect object PonebinMetadataProvider {
    suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey>
}