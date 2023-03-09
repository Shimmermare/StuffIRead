package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.pastebased.PasteMetadata
import io.github.aakira.napier.Napier
import kotlinx.datetime.toKotlinInstant
import org.jsoup.Jsoup
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Paste metadata (e.g. author) information is not accessible via public API. So we have to just parse HTTP.
 * TODO: Find better way to access this lol.
 *
 * Because relying on HTML structure is fragile, all fields will be replaced with placeholders if extraction failed.
 */
actual object PastebinMetadataProvider {
    // E.g. Thursday 27th of June 2013 10:51:07 PM CDT
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE d['st']['nd']['rd']['th'] 'of' MMMM yyyy hh:mm:ss a z")

    actual suspend fun get(pasteKey: PasteKey): PasteMetadata<PasteKey> {
        val html = Jsoup.connect(PastebinImporter.getPasteUrl(pasteKey)).get()
        val infoElement = html.select(".info-bottom")

        val author = try {
            infoElement.select(".username a")[0]!!.text()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse paste author for $pasteKey" }
            "Failed to parse author"
        }

        val name = try {
            html.selectFirst("head title")!!.text().removeSuffix(" - Pastebin.com")
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse paste name for $pasteKey" }
            "Failed to parse name"
        }

        val date = try {
            val textDate = infoElement.select(".date span")[0].attr("title")
            Instant.from(DATE_FORMAT.parse(textDate)).toKotlinInstant()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse added date for $pasteKey" }
            null
        }

        return PasteMetadata(
            id = pasteKey,
            author = author,
            name = name,
            addedDate = date
        )
    }
}