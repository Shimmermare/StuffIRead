package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.UrlParser

object PastebinUrlParser : UrlParser<PasteKey> {
    private val regex = Regex(".*pastebin\\.com/(?:raw/)?(.+)")

    override fun matches(url: String): Boolean {
        return regex.matches(url)
    }

    override fun parse(url: String): PasteKey {
        val match = regex.matchEntire(url) ?: throw IllegalArgumentException("Given URL is not a pastebin paste: $url")
        return PasteKey(match.groupValues[1])
    }
}