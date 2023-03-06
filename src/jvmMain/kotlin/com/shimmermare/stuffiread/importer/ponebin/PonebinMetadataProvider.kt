package com.shimmermare.stuffiread.importer.ponebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata
import io.github.aakira.napier.Napier
import kotlinx.datetime.toKotlinInstant
import org.jsoup.Jsoup
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Same problem as Pastebin - poneb.in has no API to get meta, so we have to parse HTML.
 *
 * Because relying on HTML structure inevitably will lead to problems later,
 * the only "required" field is paste name. Other fields will be replaced with placeholders if failed.
 */
actual object PonebinMetadataProvider {
    // E.g. 2014.11.29 19:04:57 UTC
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss z")

    actual suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey> {
        val html = Jsoup.connect(PonebinImporter.getPasteUrl(pasteKey)).get()

        val author = try {
            html.select(".author > a")[0].text() ?: error("No author")
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse paste author for $pasteKey" }
            "Failed to get author"
        }

        val name = html.select(".title")[0].text()

        val (addedDate, modifiedDate) = try {
            val texts = html.select(".code > span")
                .asSequence()
                .filter { it.hasText() }
                .map { it.text().trim() }
                .toList()

            val addedDateText = texts.first { it.startsWith("Created: ") }.removePrefix("Created: ")
            val addedDate = Instant.from(DATE_FORMAT.parse(addedDateText)).toKotlinInstant()
            val modifiedDateText = texts.first { it.startsWith("Last modified: ") }.removePrefix("Last modified: ")
            val modifiedDate = Instant.from(DATE_FORMAT.parse(modifiedDateText)).toKotlinInstant()
            addedDate to modifiedDate
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse added date for $pasteKey" }
            null to null
        }

        return PasteMetadata(
            id = pasteKey,
            author = author,
            name = name,
            addedDate = addedDate,
            modifiedDate = modifiedDate,
        )
    }
}