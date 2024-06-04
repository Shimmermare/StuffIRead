package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata
import io.github.aakira.napier.Napier
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Same problem as Pastebin - ponepaste has no API to get meta, so we have to parse HTML.
 *
 *  Because relying on HTML structure is fragile, all fields will be replaced with placeholders if extraction failed.
 */
actual object PonepasteMetadataProvider {
    // E.g.  2024-05-15 20:30:59
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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

        val addedDate = try {
            parseDate(html, "Created: ")
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse added date for $pasteId" }
            null
        }

        val modifiedDate = try {
            parseDate(html, "Updated: ")
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse updated date for $pasteId" }
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
            author,
            name,
            addedDate,
            modifiedDate,
            tags
        )
    }

    private fun parseDate(html: Document, prefix: String): Instant {
        val textDate = html.selectFirst("small.title")!!.textNodes()
            .map { it.text().trim() }
            .first { it.startsWith(prefix) }
            .removePrefix(prefix)
        // Looks like time is always UTC0
        return LocalDateTime.from(DATE_FORMAT.parse(textDate))
            .toInstant(ZoneOffset.UTC)
            .toKotlinInstant()
    }
}