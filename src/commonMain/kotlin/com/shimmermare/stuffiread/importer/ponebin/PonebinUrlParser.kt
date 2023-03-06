package com.shimmermare.stuffiread.importer.ponebin

import com.shimmermare.stuffiread.importer.UrlParser

object PonebinUrlParser : UrlParser<PasteKey> {
    private val regex = Regex(".*poneb\\.in/(?:raw/)?(.+)")

    override fun matches(url: String): Boolean {
        return regex.matches(url)
    }

    override fun parse(url: String): PasteKey {
        val match = regex.matchEntire(url) ?: throw IllegalArgumentException("Given URL is not a ponebin paste: $url")
        return PasteKey(match.groupValues[1])
    }
}