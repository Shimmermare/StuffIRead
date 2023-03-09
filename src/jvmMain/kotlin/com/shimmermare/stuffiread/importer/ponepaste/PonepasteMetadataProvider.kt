package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata
import io.github.aakira.napier.Napier
import kotlinx.datetime.toKotlinInstant
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Same problem as Pastebin - ponepaste has no API to get meta, so we have to parse HTML.
 *
 *  Because relying on HTML structure is fragile, all fields will be replaced with placeholders if extraction failed.
 */
actual object PonepasteMetadataProvider {
    // E.g. 16th July 2021 09:31:33 PM
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("d['st']['nd']['rd']['th'] MMMM yyyy hh:mm:ss a")

    actual suspend fun get(pasteId: PonepasteId): PasteMetadata<PonepasteId> {
        val html = Jsoup.connect(PonepasteImporter.getPasteUrl(pasteId)).get()

        val author = try {
            html.selectFirst("a[href^='/user/']")!!.text()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse paste author for $pasteId" }
            "Failed to get author"
        }

        val name = try {
            html.selectFirst("h1.title")!!.text()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse paste author for $pasteId" }
            "Failed to parse name"
        }

        val date = try {
            val textDate = html.selectFirst("small.title")!!.textNodes()
                .map { it.text().trim() }
                .first { it.startsWith("Created: ") }
                .removePrefix("Created: ")
            // Looks like time is always UTC0
            LocalDateTime.from(DATE_FORMAT.parse(textDate))
                .toInstant(ZoneOffset.UTC)
                .toKotlinInstant()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse added date for $pasteId" }
            null
        }

        val tags = try {
            html.select("a[href^='/archive?q='] > span.tag").mapTo(mutableSetOf()) { it.text() }
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse tags for $pasteId" }
            emptySet()
        }

        return PasteMetadata(
            id = pasteId,
            author = author,
            name = name,
            addedDate = date,
            tags = tags
        )
    }
}