package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImporter
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnUrlParser
import com.shimmermare.stuffiread.importer.pastebin.PastebinImporter
import com.shimmermare.stuffiread.importer.pastebin.PastebinUrlParser
import com.shimmermare.stuffiread.importer.ponebin.PonebinImporter
import com.shimmermare.stuffiread.importer.ponebin.PonebinUrlParser
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteImporter
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteUrlParser

enum class ImportSource(
    val urlParser: UrlParser<*>,
    val importer: StoryImporter<out ImportSettings>,
    val ponyIntegration: Boolean = false,
) {
    PASTEBIN(PastebinUrlParser, PastebinImporter),
    PONEPASTE(PonepasteUrlParser, PonepasteImporter, ponyIntegration = true),
    PONEBIN(PonebinUrlParser, PonebinImporter, ponyIntegration = true),
    ARCHIVE_OF_OUR_OWN(ArchiveOfOurOwnUrlParser, ArchiveOfOurOwnImporter),
    // TODO FIMFICTION(..., ..., ponyIntegration = true),
    // TODO FICBOOK(..., ...), - also maybe add settings bool to disable non-english sources?
    // TODO PONYFICTION(PastebinUrlParser, PastebinImporter, ponyIntegration = true),
}