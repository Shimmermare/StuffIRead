package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImporter
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnUrlParser
import com.shimmermare.stuffiread.importer.pastebin.PastebinImporter
import com.shimmermare.stuffiread.importer.pastebin.PastebinUrlParser
import com.shimmermare.stuffiread.importer.ponebin.PonebinImporter
import com.shimmermare.stuffiread.importer.ponebin.PonebinUrlParser
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteImporter
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteUrlParser
import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag

enum class ImportSource(
    val urlParser: UrlParser<*>,
    val importer: StoryImporter<out ImportSettings>,
    val ponyIntegration: Boolean = false,
    val locale: Locale,
) {
    ARCHIVE_OF_OUR_OWN(ArchiveOfOurOwnUrlParser, ArchiveOfOurOwnImporter, locale = forLocaleTag("en")),
    PASTEBIN(PastebinUrlParser, PastebinImporter, locale = forLocaleTag("en")),
    PONEPASTE(PonepasteUrlParser, PonepasteImporter, ponyIntegration = true, locale = forLocaleTag("en")),
    PONEBIN(PonebinUrlParser, PonebinImporter, ponyIntegration = true, locale = forLocaleTag("en")),
    // TODO FIMFICTION(..., ..., ponyIntegration = true, locale = forLocaleTag("en")),
    // TODO FICBOOK(..., ..., locale = forLocaleTag("ru")),
    // TODO PONYFICTION(..., ..., ponyIntegration = true, locale = forLocaleTag("ru")),
}