package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.importer.pastebin.PastebinImporter
import com.shimmermare.stuffiread.importer.pastebin.PastebinUrlParser
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteImporter
import com.shimmermare.stuffiread.importer.ponepaste.PonepasteUrlParser

enum class ImportSource(
    val urlParser: UrlParser<*>,
    val importer: StoryImporter<out ImportSettings>,
) {
    ARCHIVE_OF_OUR_OWN(PastebinUrlParser, PastebinImporter),
    PASTEBIN(PastebinUrlParser, PastebinImporter),
    PONEBIN(PastebinUrlParser, PastebinImporter),
    FIMFICTION(PastebinUrlParser, PastebinImporter),
    PONEPASTE(PonepasteUrlParser, PonepasteImporter),
    FICBOOK(PastebinUrlParser, PastebinImporter),
    PONYFICTION(PastebinUrlParser, PastebinImporter),
}