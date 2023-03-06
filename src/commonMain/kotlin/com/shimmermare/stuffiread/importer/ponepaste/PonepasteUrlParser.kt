package com.shimmermare.stuffiread.importer.ponepaste

import com.shimmermare.stuffiread.importer.UrlParser

object PonepasteUrlParser : UrlParser<PonepasteId> {
    private val regex = Regex(".*ponepaste\\.org/(?:raw/)?(\\d+)")

    override fun matches(url: String): Boolean {
        return regex.matches(url)
    }

    override fun parse(url: String): PonepasteId {
        val match = regex.matchEntire(url) ?: throw IllegalArgumentException("Given URL is not a ponepaste paste: $url")
        return PonepasteId(match.groupValues[1].toUInt())
    }
}