package com.shimmermare.stuffiread.importer.ponyfiction

import com.shimmermare.stuffiread.util.AppJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

actual object PonyfictionMetaDumpParser {
    actual fun getMetaMappings(bytes: ByteArray): MetaMappings {
        var tagNamesById: Map<UInt, String> = emptyMap()
        var characterNamesById: Map<UInt, String> = emptyMap()

        ByteArrayInputStream(bytes).use { bytesIn ->
            ZipInputStream(bytesIn).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    when (entry.name) {
                        "dump/tag_dump.jsonl" -> tagNamesById = parseJsonLinesEntry(zipIn)
                        "dump/character_dump.jsonl" -> characterNamesById = parseJsonLinesEntry(zipIn)
                        else -> {}
                    }
                    entry = zipIn.nextEntry
                }
            }
        }

        return MetaMappings(tagNamesById, characterNamesById)
    }

    private fun parseJsonLinesEntry(zipIn: ZipInputStream): Map<UInt, String> {
        val scanner = Scanner(zipIn, Charsets.UTF_8)
        val mappings = mutableMapOf<UInt, String>()
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            val namedEntry = AppJson.decodeFromString<NamedEntry>(line)
            mappings[namedEntry.id] = namedEntry.name
        }
        return mappings
    }

    @Serializable
    private data class NamedEntry(
        val id: UInt,
        val name: String,
    )
}