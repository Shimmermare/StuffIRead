package com.shimmermare.stuffiread.importer.ponyfiction

import com.shimmermare.stuffiread.importer.UrlParser

object PonyfictionUrlParser : UrlParser<PonyfictionStoryId> {
    private val regex = Regex(".*ponyfiction\\.org/story/(\\d+).*")

    override fun matches(url: String): Boolean {
        return regex.matches(url)
    }

    override fun parse(url: String): PonyfictionStoryId {
        val match = regex.matchEntire(url)
            ?: throw IllegalArgumentException("Given URL is not a ponyfiction story: $url")
        return PonyfictionStoryId(match.groupValues[1].toUInt())
    }
}