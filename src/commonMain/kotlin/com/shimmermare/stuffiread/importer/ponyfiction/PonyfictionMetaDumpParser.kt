package com.shimmermare.stuffiread.importer.ponyfiction

/**
 * Extrated to header because kotlin multiplatform has no native zip lib.
 */
expect object PonyfictionMetaDumpParser {
    fun getMetaMappings(bytes: ByteArray): MetaMappings
}

data class MetaMappings(
    val tagNamesById: Map<UInt, String>,
    val characterNamesById: Map<UInt, String>,
)