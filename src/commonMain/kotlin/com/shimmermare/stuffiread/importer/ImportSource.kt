package com.shimmermare.stuffiread.importer

import com.shimmermare.stuffiread.importer.pastebin.PastebinImporter

enum class ImportSource(
    val importer: StoryImporter<out ImportSettings>,
) {
    ARCHIVE_OF_OUR_OWN(PastebinImporter),
    PASTEBIN(PastebinImporter),
    PONEBIN(PastebinImporter),
    FIMFICTION(PastebinImporter),
    PONEPASTE(PastebinImporter),
    FICBOOK(PastebinImporter),
    PONYFICTION(PastebinImporter),
}